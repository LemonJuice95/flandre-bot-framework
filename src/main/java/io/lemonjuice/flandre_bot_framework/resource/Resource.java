package io.lemonjuice.flandre_bot_framework.resource;

import lombok.extern.log4j.Log4j2;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

@Log4j2
public abstract class Resource<T> implements Supplier<T> {
    private final String resPath;
    private final T dummyValue;
    private final Source source;

    private volatile T res;

    public Resource(String resPath, T dummyValue) {
        this(resPath, dummyValue, Source.CLASS_PATH);
    }

    public Resource(String resPath, T dummyValue, Source source) {
        this.resPath = resPath;
        this.dummyValue = dummyValue;
        this.source = source;
    }

    public T get() {
        if (this.res == null) {
            synchronized (this) {
                if (this.res == null) {
                    this.init();
                }
            }
        }
        return this.res;
    }

    public synchronized void init() {
        try (InputStream input = this.source.getStream(this.resPath)) {
            log.info("正在加载资源: {}", this.resPath);
            this.res = this.load(input);
        } catch (Exception e) {
            log.error("加载资源失败: {}", this.resPath, e);
            this.res = this.dummyValue;
        }
    }

    protected abstract T load(InputStream input) throws IOException;

    public enum Source {
        CLASS_PATH,
        FILE_SYSTEM;

        public InputStream getStream(String resPath) throws IOException {
            return switch (this) {
                case CLASS_PATH -> Resource.class.getClassLoader().getResourceAsStream(resPath);
                case FILE_SYSTEM -> new FileInputStream(resPath);
                default -> throw new IllegalStateException("未定义的资源来源: " + this);
            };
        }
    }
}