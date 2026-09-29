package io.lemonjuice.flandre_bot_framework.event.bus;

import java.util.function.Supplier;

public enum BusMode {
    SYNC(SyncEventBus::new),
    PARALLEL(ParallelEventBus::new),
    ASYNC(AsyncEventBus::new);

    private final Supplier<IEventBus> busProvider;

    private BusMode(Supplier<IEventBus> busProvider) {
        this.busProvider = busProvider;
    }

    public IEventBus getEventBus() {
        return this.busProvider.get();
    }
}
