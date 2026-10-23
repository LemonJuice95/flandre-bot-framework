package io.lemonjuice.flandre_bot_framework.config;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigItem<T> implements Supplier<T> {
    private volatile T value = null;
    private volatile boolean loaded = false;
    private final Supplier<T> provider;

    public ConfigItem(Supplier<T> provider) {
        this.provider = provider;
    }

    public ConfigItem(Function<String, T> provider, String key) {
        this(() -> provider.apply(key));
    }

    public ConfigItem(BiFunction<String, T, T> provider, String key, T defaultValue) {
        this(() -> provider.apply(key, defaultValue));
    }

    @Override
    public T get() {
        if (!this.loaded) {
            synchronized (this) {
                if (!this.loaded) {
                    this.value = this.provider.get();
                    this.loaded = true;
                }
            }
        }

        return this.value;
    }

    public boolean changed() {
        boolean result = false;

        if(!this.loaded) {
            synchronized (this) {
                if(!this.loaded) {
                    T value = this.provider.get();
                    result = !Objects.equals(value, this.value);
                    this.value = value;
                    this.loaded = true;
                }
            }
        }

        return result;
    }

    public synchronized void reset() {
        this.loaded = false;
    }
}
