package io.lemonjuice.flandre_bot_framework.event.bus;

import io.lemonjuice.flandre_bot_framework.event.Event;
import io.lemonjuice.flandre_bot_framework.event.ICancelableEvent;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class AsyncEventBus extends ParallelEventBus {
    @Override
    public void post(Event event) {
        if(event instanceof ICancelableEvent) {
            super.post(event);
            return;
        }

        Class<?> clazz = event.getClass();
        while(!clazz.equals(Object.class)) {
            List<Subscriber> subscribers = this.subscribers.get(clazz);
            for(Subscriber sub : subscribers) {
                Thread.startVirtualThread(() -> {
                    try {
                        sub.post(event);
                    } catch (Throwable e) {
                        log.error("无法执行订阅者方法\"{}.{}\"", sub.target.getClass().getSimpleName(), sub.method.getName(), e);
                    }
                });
            }
            clazz = clazz.getSuperclass();
        }
    }
}
