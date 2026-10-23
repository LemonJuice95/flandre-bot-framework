package io.lemonjuice.flandre_bot_framework.event.bus;

import io.lemonjuice.flandre_bot_framework.event.Event;
import io.lemonjuice.flandre_bot_framework.event.ICancelableEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public interface IEventBus {
    public void register(Object object);
    public void unregister(Object object);
    public void post(Event event);
    public boolean postCancellable(ICancelableEvent event);
    public ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<Subscriber>> getSubscribers();
    public void addSubscribers(ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<Subscriber>> subscribers);
}
