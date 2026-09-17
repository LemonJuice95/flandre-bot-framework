package io.lemonjuice.flandre_bot_framework.event.bus;

import io.lemonjuice.flandre_bot_framework.event.Event;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

class Subscriber {
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
