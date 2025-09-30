package io.lemonjuice.flandre_bot_framework.event;

import io.lemonjuice.flandre_bot_framework.event.annotation.EventSubscriber;
import io.lemonjuice.flandre_bot_framework.event.bus.SyncEventBus;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.util.Set;

@Log4j2
public class BotEventBus {
    @Getter
    private static BotEventBus instance;

    private final SyncEventBus bus = new SyncEventBus();

    private BotEventBus() {
    }

    public static void init() {
        log.info("正在创建事件总线实例");
        instance = new BotEventBus();
        registerSubscribers();
        log.info("事件总线初始化完成！");
    }

    //TODO 先用着 以后看看能不能写个编译时注解处理器
    private static void registerSubscribers() {
        log.info("正在注册所有事件监听器");
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .addScanners(Scanners.TypesAnnotated)
                .setUrls(ClasspathHelper.forJavaClassPath()));
        Set<Class<?>> subscribers = reflections.getTypesAnnotatedWith(EventSubscriber.class);
        for(Class<?> clazz : subscribers) {
            try {
                Constructor<?> constructor = clazz.getConstructor();
                Object object = constructor.newInstance();
                instance.bus.register(object);
            } catch (Exception e) {
                log.error("注册事件监听器失败，请检查\"{}\"类中是否具有注册所需的无参构造函数", clazz.getName());
            }
        }
    }

    public static void post(Event event) {
        try {
            instance.bus.post(event);
        } catch (NullPointerException e) {
            log.warn("事件总线未初始化完成", e);
        }
    }

    public static boolean postCancelable(ICancelableEvent event) {
        try {
            if(event instanceof Event event_) {
                instance.bus.post(event_);
            }
        } catch (NullPointerException e) {
            log.warn("事件总线未初始化完成", e);
        }
        return event.isCancelled();
    }

    public static void unregister(Object object) {
        instance.bus.unregister(object);
    }
}