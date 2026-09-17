package io.lemonjuice.flandre_bot_framework.event.bus;

import io.lemonjuice.flandre_bot_framework.event.Event;
import io.lemonjuice.flandre_bot_framework.event.ICancelableEvent;
import io.lemonjuice.flandre_bot_framework.event.annotation.SubscribeEvent;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Log4j2
public abstract class AbstractEventBus implements IEventBus {
    protected final ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<Subscriber>> subscribers = new ConcurrentHashMap<>();

    public void register(Object object) {
        Method[] methods = object.getClass().getMethods();
        for(Method method : methods) {
            if(method.isAnnotationPresent(SubscribeEvent.class)) {
                Class<?>[] params = method.getParameterTypes();
                if(params.length == 1 && Event.class.isAssignableFrom(params[0])) {
                    Class<?> eventClass = params[0];
                    CopyOnWriteArrayList<Subscriber> list = this.getOrCreateList(eventClass);
                    method.setAccessible(true);
                    try {
                        list.add(new Subscriber(object, method));
                    } catch (IllegalAccessException e) {
                        log.warn("{}中的{}方法无法从外部访问，无法注册", object.getClass().getSimpleName(), method.getName());
                    }
                } else {
                    log.warn("{}中的{}方法不符合订阅者方法的条件，无法注册", object.getClass().getSimpleName(), method.getName());
                }
            }
        }
    }

    @Override
    public boolean postCancellable(ICancelableEvent event) {
        if(event instanceof Event event_) {
            this.post(event_);
        }
        return event.isCancelled();
    }

    public void unregister(Object object) {
        this.subscribers.values().forEach((list) -> {
            list.removeIf(s -> s.target.equals(object));
        });
    }

    private CopyOnWriteArrayList<Subscriber> getOrCreateList(Class<?> clazz) {
        return this.subscribers.computeIfAbsent(clazz, k -> new CopyOnWriteArrayList<>());
    }
}
