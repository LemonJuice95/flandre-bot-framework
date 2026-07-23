package io.lemonjuice.flandre_bot_framework.event.bus;

import io.lemonjuice.flandre_bot_framework.event.Event;
import io.lemonjuice.flandre_bot_framework.event.annotation.SubscribeEvent;
import io.lemonjuice.flandre_bot_framework.utils.data.Pair;
import lombok.extern.log4j.Log4j2;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Log4j2
public class SyncEventBus {
    private final ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<Subscriber>> subscribers = new ConcurrentHashMap<>();

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

    public void unregister(Object object) {
        this.subscribers.values().forEach((list) -> {
            list.removeIf(s -> s.target.equals(object));
        });
    }

    private CopyOnWriteArrayList<Subscriber> getOrCreateList(Class<?> clazz) {
        return this.subscribers.computeIfAbsent(clazz, k -> new CopyOnWriteArrayList<>());
    }

    private static class Subscriber {
        public final Object target;
        public final Method method;
        public final MethodHandle methodHandle;

        public Subscriber(Object target, Method method) throws IllegalAccessException {
            this.target = target;
            this.method = method;

            MethodHandles.Lookup lookup = MethodHandles.lookup();
            this.methodHandle = lookup.unreflect(method).bindTo(target);
        }

        public void post(Event event) throws Throwable {
            this.methodHandle.invoke(event);
        }
    }
}
