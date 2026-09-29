package io.lemonjuice.flandre_bot_framework.event;

import io.lemonjuice.flandre_bot_framework.config.BotBasicConfig;
import io.lemonjuice.flandre_bot_framework.event.annotation.EventSubscriber;
import io.lemonjuice.flandre_bot_framework.event.bus.IEventBus;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

@Log4j2
public class BotEventBus {
    @Getter
    private static BotEventBus instance;

    @Getter
    private final IEventBus bus;

    private BotEventBus() {
        this.bus = BotBasicConfig.EVENT_BUS_MODE.get().getEventBus();
    }

    public static void init() {
        log.info("正在创建事件总线实例");
        instance = new BotEventBus();
        registerSubscribers();
        log.info("事件总线初始化完成！");
    }

    private static void registerSubscribers() {
        log.info("正在注册所有事件监听器");
        if(BotBasicConfig.SCAN_CLASSPATH.get()) {
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .addScanners(Scanners.TypesAnnotated)
                    .setUrls(ClasspathHelper.forJavaClassPath()));
            Set<Class<?>> subscribers = reflections.getTypesAnnotatedWith(EventSubscriber.class);
            for (Class<?> clazz : subscribers) {
                try {
                    Constructor<?> constructor = clazz.getConstructor();
                    Object object = constructor.newInstance();
                    instance.bus.register(object);
                } catch (Exception e_) {
                    log.error("注册事件监听器失败，请检查\"{}\"类中是否具有注册所需的无参构造函数", clazz.getName());
                }
            }
            return;
        }

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackage("io.lemonjuice.flandre_bot_framework.generated.subscriber")
                        .addScanners(Scanners.SubTypes)
        );
        Set<Class<? extends ISubscriberRegister>> registers = reflections.getSubTypesOf(ISubscriberRegister.class);
        log.info("发现 {} 个订阅者注册器", registers.size());
        for(Class<? extends ISubscriberRegister> clazz : registers) {
            try {
                Constructor<? extends ISubscriberRegister> constructor = clazz.getConstructor();
                ISubscriberRegister registerInstance = constructor.newInstance();
                registerInstance.register();
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                log.error(String.format("生成的%s类的订阅者注册器执行失败", clazz.getSimpleName()), e instanceof InvocationTargetException ? e.getCause() : e);
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
            return instance.bus.postCancellable(event);
        } catch (NullPointerException e) {
            log.warn("事件总线未初始化完成", e);
        }
        return event.isCancelled();
    }

    public static void register(Object object) {
        instance.bus.register(object);
    }

    public static void unregister(Object object) {
        instance.bus.unregister(object);
    }
}