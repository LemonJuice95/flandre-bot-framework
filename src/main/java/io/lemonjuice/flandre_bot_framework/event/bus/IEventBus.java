package io.lemonjuice.flandre_bot_framework.event.bus;

import io.lemonjuice.flandre_bot_framework.event.Event;
import io.lemonjuice.flandre_bot_framework.event.ICancelableEvent;

public interface IEventBus {
    public void register(Object object);
    public void unregister(Object object);
    public void post(Event event);
    public boolean postCancellable(ICancelableEvent event);
}
