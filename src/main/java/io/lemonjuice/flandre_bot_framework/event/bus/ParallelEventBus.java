package io.lemonjuice.flandre_bot_framework.event.bus;

import io.lemonjuice.flandre_bot_framework.event.Event;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Log4j2
public class ParallelEventBus extends AbstractEventBus {
    @Override
    public void post(Event event) {
        Class<?> clazz = event.getClass();
        List<Subscriber> subscribers = new ArrayList<>();
        while (clazz != Object.class) {
            if(this.subscribers.get(clazz) != null) {
                subscribers.addAll(this.subscribers.get(clazz));
            }
            clazz = clazz.getSuperclass();
        }

        CountDownLatch finished = new CountDownLatch(subscribers.size());
        subscribers.forEach(sub -> {
            Thread.startVirtualThread(() -> {
                try {
                    sub.post(event);
                } catch (Throwable e) {
                    log.error("无法执行订阅者方法\"{}.{}\"", sub.target.getClass().getSimpleName(), sub.method.getName(), e);
                } finally {
                    finished.countDown();
                }
            });
        });

        try {
            finished.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
