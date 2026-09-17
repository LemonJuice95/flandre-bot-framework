package io.lemonjuice.flandre_bot_framework.event.bus;

import io.lemonjuice.flandre_bot_framework.event.Event;
import io.lemonjuice.flandre_bot_framework.event.annotation.SubscribeEvent;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Log4j2
public class SyncEventBus extends AbstractEventBus {
    public void post(Event event) {
        Class<?> clazz = event.getClass();
        while (clazz != Object.class) {
            List<Subscriber> subscribers = this.subscribers.get(clazz);
            if(subscribers != null) {
                for(Subscriber subscriber : subscribers) {
                    try {
                        subscriber.post(event);
                    } catch (Throwable e) {
                        log.error("无法执行订阅者方法\"{}.{}\"", subscriber.target.getClass().getSimpleName(), subscriber.method.getName(), e);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}
