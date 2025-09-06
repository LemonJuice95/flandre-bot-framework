package io.lemonjuice.flandre_bot;

import io.lemonjuice.flandre_bot.config.BotBasicConfig;
import io.lemonjuice.flandre_bot.config.BasicConfigFileChecker;
import io.lemonjuice.flandre_bot.event.BotEventBus;
import io.lemonjuice.flandre_bot.event.meta.BotInitEvent;
import io.lemonjuice.flandre_bot.event.meta.PluginLoadEvent;
import io.lemonjuice.flandre_bot.network.WSClientCore;
import io.lemonjuice.flandre_bot.network.WSReconnect;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.CountDownLatch;

@Log4j2
public class FlandreBot {
    private static final CountDownLatch keepAlive = new CountDownLatch(1);

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        log.info("正在启动Bot: {}", BotBasicConfig.BOT_NAME.get());
        
        BotEventBus.init();
        Runtime.getRuntime().addShutdownHook(new Thread(new Stop()));

        BasicConfigFileChecker.check();
        BotBasicConfig.read();

        BotEventBus.post(new PluginLoadEvent());
        BotEventBus.post(new BotInitEvent());

        if(!WSClientCore.connect(BotBasicConfig.WS_URL.get(), BotBasicConfig.WS_TOKEN.get())) {
            new Thread(new WSReconnect()).start();
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
