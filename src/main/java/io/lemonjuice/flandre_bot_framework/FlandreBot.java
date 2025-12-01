package io.lemonjuice.flandre_bot_framework;

import io.lemonjuice.flandre_bot_framework.config.BotBasicConfig;
import io.lemonjuice.flandre_bot_framework.config.BasicConfigFileInit;
import io.lemonjuice.flandre_bot_framework.console.BotConsole;
import io.lemonjuice.flandre_bot_framework.console.ConsoleListener;
import io.lemonjuice.flandre_bot_framework.console.original.OriginalConsoleCommands;
import io.lemonjuice.flandre_bot_framework.event.BotEventBus;
import io.lemonjuice.flandre_bot_framework.event.meta.BotInitEvent;
import io.lemonjuice.flandre_bot_framework.event.meta.PluginRegisterEvent;
import io.lemonjuice.flandre_bot_framework.handler.ReceivingMessageHandler;
import io.lemonjuice.flandre_bot_framework.lifecycle.Stop;
import io.lemonjuice.flandre_bot_framework.network.NetworkContainer;
import io.lemonjuice.flandre_bot_framework.plugins.PluginsLoadingProcessor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Log4j2
public class FlandreBot {
    private static final CountDownLatch keepAlive = new CountDownLatch(1);
    private static final Thread consoleListenerThread = new Thread(new ConsoleListener(), "Console");
    private static final Thread stopThread = new Thread(new Stop(), "Shutdown");

    private static boolean keyConfigLost = false;
    private static final List<String> configLostMessages = new ArrayList<>();

    public static void main(String[] args) {
        stopThread.setDaemon(false);
        Runtime.getRuntime().addShutdownHook(stopThread);
        configureLogger();
        FrameworkInfo.init();
        start();

        try {
            keepAlive.await();
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
    }

    public static void markKeyConfigLost(String message) {
        keyConfigLost = true;
        configLostMessages.add(message);
    }

    private static void configureLogger() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        PluginManager pluginManager = new PluginManager(Appender.ELEMENT_TYPE);
        pluginManager.collectPlugins(List.of("io.lemonjuice.flandre_bot_framework.console"));
    }

    public static void start() {
        long startTime = System.currentTimeMillis();

        System.out.println("Flandre Bot Framework v" + FrameworkInfo.getInstance().version);
        System.out.println(FrameworkInfo.logo);

        BasicConfigFileInit.init();

        BotConsole.init();
        OriginalConsoleCommands.ORIGINAL_CONSOLE_COMMANDS.load();
        BotEventBus.init();

        PluginsLoadingProcessor pluginLoader = new PluginsLoadingProcessor();
        BotEventBus.post(new PluginRegisterEvent(pluginLoader));
        pluginLoader.initConfigs();

        if(keyConfigLost) {
            configLostMessages.forEach(log::warn);
            stop();
        }

        pluginLoader.loadPlugins();

        ReceivingMessageHandler.init();
        BotEventBus.post(new BotInitEvent());
        NetworkContainer.init(BotBasicConfig.NETWORK_MODE.get());

        float usedTime = (System.currentTimeMillis() - startTime) / 1000.0F;
        log.info(String.format("Bot已启动！(%.2fs)", usedTime));

        if(BotConsole.isAvailable()) {
            consoleListenerThread.start();
        } else {
            System.out.println("警告: 无法使用控制台命令系统");
        }
    }

    public static void stop() {
        System.exit(0);
    }
}
