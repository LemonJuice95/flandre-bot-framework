package io.lemonjuice.flandre_bot_framework.event.bus;

import io.lemonjuice.flandre_bot_framework.event.Event;
import io.lemonjuice.flandre_bot_framework.event.annotation.SubscribeEvent;
import io.lemonjuice.flandre_bot_framework.utils.data.Pair;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Log4j2
public class SyncEventBus {
    private final ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<Pair<Object, Method>>> subscribers = new ConcurrentHashMap<>();

    public void register(Object object) {
        Method[] methods = object.getClass().getMethods();
        for(Method method : methods) {
            if(method.isAnnotationPresent(SubscribeEvent.class)) {
                Class<?>[] params = method.getParameterTypes();
                if(params.length == 1 && Event.class.isAssignableFrom(params[0])) {
                    Class<?> eventClass = params[0];
                    CopyOnWriteArrayList<Pair<Object, Method>> list = this.getOrCreateList(eventClass);
                    method.setAccessible(true);
                    list.add(Pair.of(object, method));
                } else {
                    log.warn("{}中的{}方法不符合订阅者方法的条件，无法注册", object.getClass().getSimpleName(), method.getName());
                }
            }
        }
    }

    public void post(Event event) {
        Class<?> clazz = event.getClass();
        while (clazz != Object.class) {
            List<Pair<Object, Method>> subscriberMethods = this.subscribers.get(clazz);
            if(subscriberMethods != null) {
                for(Pair<Object, Method> pair : subscriberMethods) {
                    Method method = pair.getSecond();
                    try {
                        method.invoke(pair.getFirst(), event);
                    } catch (Exception e) {
                        log.error("无法执行订阅者方法\"{}.{}\"", pair.getFirst().getClass().getSimpleName(), method.getName(), e);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    public void unregister(Object object) {
        this.subscribers.values().forEach((list) -> {
            list.removeIf(pair -> pair.getFirst().equals(object));
        });
    }

    private CopyOnWriteArrayList<Pair<Object, Method>> getOrCreateList(Class<?> clazz) {
        return this.subscribers.computeIfAbsent(clazz, k -> new CopyOnWriteArrayList<>());
    }
}
