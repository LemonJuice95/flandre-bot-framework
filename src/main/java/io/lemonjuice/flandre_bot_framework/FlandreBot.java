package io.lemonjuice.flandre_bot_framework;

import io.lemonjuice.flandre_bot_framework.config.BotBasicConfig;
import io.lemonjuice.flandre_bot_framework.config.BasicConfigFileChecker;
import io.lemonjuice.flandre_bot_framework.console.BotConsole;
import io.lemonjuice.flandre_bot_framework.console.ConsoleListener;
import io.lemonjuice.flandre_bot_framework.console.TerminalConsoleAppender;
import io.lemonjuice.flandre_bot_framework.console.original.OriginalConsoleCommands;
import io.lemonjuice.flandre_bot_framework.event.BotEventBus;
import io.lemonjuice.flandre_bot_framework.event.meta.BotInitEvent;
import io.lemonjuice.flandre_bot_framework.event.meta.PluginRegisterEvent;
import io.lemonjuice.flandre_bot_framework.network.WSClientCore;
import io.lemonjuice.flandre_bot_framework.network.WSReconnect;
import io.lemonjuice.flandre_bot_framework.plugins.PluginsLoadingProcessor;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.CountDownLatch;

@Log4j2
public class FlandreBot {
    private static final CountDownLatch keepAlive = new CountDownLatch(1);
    private static final Thread consoleListenerThread = new Thread(new ConsoleListener());

    public static void main(String[] args) {
        FrameworkInfo.init();
        start();
    }

    public static String getName() {
        return BotBasicConfig.BOT_NAME.get();
    }

    public static void start() {
        System.out.println("Flandre Bot Framework v" + FrameworkInfo.getInstance().version);
        System.out.println(FrameworkInfo.logo);
        log.info("正在启动Bot: {}", getName());

        Runtime.getRuntime().addShutdownHook(new Thread(new Stop()));

        BotConsole.init();
        TerminalConsoleAppender.bindConsole(BotConsole.getInstance());

        BotEventBus.init();

        BasicConfigFileChecker.check();
        BotBasicConfig.read();

//        BotEventBus.post(new PluginLoadEvent());
        PluginsLoadingProcessor pluginLoader = new PluginsLoadingProcessor();
        BotEventBus.post(new PluginRegisterEvent(pluginLoader));
        pluginLoader.loadPlugins();

        OriginalConsoleCommands.ORIGINAL_CONSOLE_COMMANDS.load();
        BotEventBus.post(new BotInitEvent());

        if(!WSClientCore.connect(BotBasicConfig.WS_URL.get(), BotBasicConfig.WS_TOKEN.get())) {
            Thread.startVirtualThread(new WSReconnect());
        }

        if(BotConsole.isAvailable()) {
            consoleListenerThread.start();
        } else {
            System.out.println("警告: 无法使用控制台命令系统");
        }

        try {
            keepAlive.await();
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
    }

    public static void stop() {
        System.exit(0);
    }
}
