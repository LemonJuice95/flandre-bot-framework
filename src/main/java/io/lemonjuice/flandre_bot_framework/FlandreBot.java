package io.lemonjuice.flandre_bot_framework;

import io.lemonjuice.flandre_bot_framework.config.BotBasicConfig;
import io.lemonjuice.flandre_bot_framework.config.BasicConfigFileChecker;
import io.lemonjuice.flandre_bot_framework.console.BotConsole;
import io.lemonjuice.flandre_bot_framework.console.ConsoleListener;
import io.lemonjuice.flandre_bot_framework.console.original.OriginalConsoleCommands;
import io.lemonjuice.flandre_bot_framework.event.BotEventBus;
import io.lemonjuice.flandre_bot_framework.event.meta.BotInitEvent;
import io.lemonjuice.flandre_bot_framework.event.meta.PluginRegisterEvent;
import io.lemonjuice.flandre_bot_framework.lifecycle.Stop;
import io.lemonjuice.flandre_bot_framework.network.WSClientCore;
import io.lemonjuice.flandre_bot_framework.network.WSReconnect;
import io.lemonjuice.flandre_bot_framework.plugins.PluginsLoadingProcessor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Log4j2
public class FlandreBot {
    private static final CountDownLatch keepAlive = new CountDownLatch(1);
    private static final Thread consoleListenerThread = new Thread(new ConsoleListener(), "Console");
    private static final Thread stopThread = new Thread(new Stop(), "Shutdown");

    public static void main(String[] args) {
        stopThread.setDaemon(false);
        Runtime.getRuntime().addShutdownHook(stopThread);
        configureLog4j2();
        FrameworkInfo.init();
        start();

        try {
            keepAlive.await();
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
    }

    public static String getName() {
        return BotBasicConfig.BOT_NAME.get();
    }

    private static void configureLog4j2() {
        PluginManager pluginManager = new PluginManager(Appender.ELEMENT_TYPE);
        pluginManager.collectPlugins(List.of("io.lemonjuice.flandre_bot_framework.console"));
    }

    public static void start() {
        long startTime = System.currentTimeMillis();

        System.out.println("Flandre Bot Framework v" + FrameworkInfo.getInstance().version);
        System.out.println(FrameworkInfo.logo);

        BasicConfigFileChecker.check();
        BotBasicConfig.read();

        log.info("正在启动Bot: {}", getName());

        BotConsole.init();
        BotEventBus.init();

//        BotEventBus.post(new PluginLoadEvent());
        PluginsLoadingProcessor pluginLoader = new PluginsLoadingProcessor();
        BotEventBus.post(new PluginRegisterEvent(pluginLoader));
        pluginLoader.loadPlugins();

        OriginalConsoleCommands.ORIGINAL_CONSOLE_COMMANDS.load();
        BotEventBus.post(new BotInitEvent());

        if(!WSClientCore.connect(BotBasicConfig.WS_URL.get(), BotBasicConfig.WS_TOKEN.get())) {
            Thread.startVirtualThread(new WSReconnect());
        }

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
