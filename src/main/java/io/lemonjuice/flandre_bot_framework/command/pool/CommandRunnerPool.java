package io.lemonjuice.flandre_bot_framework.command.pool;

import io.lemonjuice.flandre_bot_framework.command.CommandRunner;
import io.lemonjuice.flandre_bot_framework.config.BotBasicConfig;
import io.lemonjuice.flandre_bot_framework.model.Message;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

@Log4j2
public class CommandRunnerPool implements ICommandRunnerPool {
    private static final long POOL_GROW_CD_NANO = 40000000000L;
    private static final AtomicBoolean WARNED = new AtomicBoolean(false);

    private final AtomicInteger capacity;
    private final AtomicInteger maximumCapacity;
    private final AtomicInteger size;
    private final Function<Message, CommandRunner> provider;
    private volatile Queue<CommandRunner> runnerPool;
    //我没招了
    //这是综合考虑现有并发量级、最长的临界区及队列状态一致性后的妥协之举
    //不过好在至少两条高频路径下的临界区长度大概率不会触发park（应该？）
    private final ReentrantLock poolOperateLock = new ReentrantLock();
    private final OnlyCASLock newRunnerLock = new OnlyCASLock();
    private final MissRateStatistic missRateStatistic = new MissRateStatistic();
    private volatile long dontGrowBefore = -1L;

    public CommandRunnerPool(int initialCapacity, int maximumCapacity, Function<Message, CommandRunner> provider) {
        this.capacity = new AtomicInteger(initialCapacity);
        this.maximumCapacity = new AtomicInteger(maximumCapacity);
        this.size = new AtomicInteger(0);
        this.provider = provider;
        this.runnerPool = new ArrayDeque<>(initialCapacity);
    }

    @Override
    public CommandRunner take(Message message) {
        CommandRunner result;
        this.poolOperateLock.lock();
        try {
            result = this.runnerPool.poll();
        } finally {
            this.poolOperateLock.unlock();
        }
        if(result == null) {
            boolean shouldNew = false;
            if(this.size.get() < this.capacity.get()) {
                this.newRunnerLock.lock();
                try {
                    if(this.size.get() < this.capacity.get()) {
                        shouldNew = true;
                        this.size.incrementAndGet();
                    }
                } finally {
                    this.newRunnerLock.unlock();
                }
            }
            if(shouldNew) {
                result = this.provider.apply(message);
            }
        }
        if(result != null) {
            result.reset(message);
        }
        this.recordBorrow(result == null);
        return result;
    }

    @Override
    public void put(CommandRunner runner) {
        runner.free();
        this.poolOperateLock.lock();
        try {
            this.runnerPool.offer(runner);
        } finally {
            this.poolOperateLock.unlock();
        }
    }

    private void recordBorrow(boolean missed) {
        double missRate = this.missRateStatistic.recordBorrowAndGetMissRate(missed);
        if(missRate > BotBasicConfig.RUNNER_POOL_MISS_RATE_THRESHOLD.get()) {
            this.grow();
        }
    }

    private void grow() {
        int capacity = this.capacity.get();
        int maxCapacity = this.maximumCapacity.get();
        if(capacity >= maxCapacity) {
            if(!WARNED.getAndSet(true)) {
                log.warn("CommandRunner对象复用池已达最大容量时，降级率仍超过阈值，请考虑增大池容量");
            }
            return;
        }
        int targetCapacity = Math.min((int)(capacity * 1.5), maxCapacity);

        long currentNano = System.nanoTime();
        if(currentNano > this.dontGrowBefore) {
            this.poolOperateLock.lock();
            try {
                if(currentNano > this.dontGrowBefore) {
                    Queue<CommandRunner> newPool = new ArrayDeque<>(targetCapacity);
                    newPool.addAll(this.runnerPool);
                    this.runnerPool = newPool;
                    this.capacity.set(targetCapacity);
                    this.dontGrowBefore = currentNano + POOL_GROW_CD_NANO;
                }
            } finally {
                this.poolOperateLock.unlock();
            }
        }
    }

    public synchronized void reloadConfig(int maximumCapacity) {
        this.maximumCapacity.set(maximumCapacity);
        WARNED.set(false);
    }

    @SuppressWarnings("NonAtomicOperationOnVolatileField")
    private static class MissRateStatistic {
        private static final long BUCKET_TIME_WINDOW_NANO = 5000000000L;

        private final int[] borrowBuckets = {0, 0, 0, 0, 0, 0, 0, 0};
        private final int[] missBuckets = {0, 0, 0, 0, 0, 0, 0, 0};
        private int borrowCount = 0;
        private int missCount = 0;
        private int currentIdx = 0;
        private volatile long bucketExpiresAfter;
        private final OnlyCASLock lock = new OnlyCASLock();

        public MissRateStatistic() {
            this.bucketExpiresAfter = System.nanoTime() + BUCKET_TIME_WINDOW_NANO;
        }

        public double recordBorrowAndGetMissRate(boolean missed) {
            if(System.nanoTime() > this.bucketExpiresAfter) {
                this.switchBucket();
            }
            double missCountSnapshot;
            double borrowCountSnapshot;
            this.lock.lock();
            try {
                this.borrowBuckets[this.currentIdx]++;
                borrowCountSnapshot = (double) (++this.borrowCount);

                if (missed) {
                    this.missBuckets[this.currentIdx]++;
                    missCountSnapshot = (double) (++this.missCount);
                } else {
                    missCountSnapshot = (double) this.missCount;
                }
            } finally {
                this.lock.unlock();
            }

            return missCountSnapshot / borrowCountSnapshot;
        }

        private void switchBucket() {
            this.lock.lock();
            try {
                long currentTime = System.nanoTime();
                if(currentTime <= this.bucketExpiresAfter) {
                    return;
                }
                int forwardBlockCount = (int)((currentTime - this.bucketExpiresAfter) / BUCKET_TIME_WINDOW_NANO + 1);

                for(int i = 1; i <= forwardBlockCount; i++) {
                    this.currentIdx = (currentIdx + 1) & 7;
                    this.borrowCount -= this.borrowBuckets[this.currentIdx];
                    this.missCount -= this.missBuckets[this.currentIdx];
                    this.borrowBuckets[this.currentIdx] = 0;
                    this.missBuckets[this.currentIdx] = 0;
                }

                this.bucketExpiresAfter += forwardBlockCount * BUCKET_TIME_WINDOW_NANO;
            } finally {
                this.lock.unlock();
            }
        }
    }

    /*
    在MissRateStatistic中的使用理由:
        考虑到单次锁操作内临界区极短（约纳秒至微秒量级）
        且开销较大的switchBucket()方法为低频路径
        采用纯自旋锁避免重锁的开销
    在CommandRunnerPool（take()方法）中的使用理由:
        使用该锁的场景仍为低频路径（需要在池中创建新对象时）
        且已通过标志位优化临界区长度
     */
    private static class OnlyCASLock {
        private final AtomicBoolean lock = new AtomicBoolean();

        public void lock() {
            do {
                while(this.lock.get()) {
                    Thread.onSpinWait();
                }
            } while(!this.lock.compareAndSet(false, true));
        }

        public void unlock() {
            this.lock.set(false);
        }
    }
}
