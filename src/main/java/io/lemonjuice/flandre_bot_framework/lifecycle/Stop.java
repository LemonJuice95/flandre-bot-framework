package io.lemonjuice.flandre_bot_framework.lifecycle;

import io.lemonjuice.flandre_bot_framework.console.BotConsole;
import io.lemonjuice.flandre_bot_framework.event.BotEventBus;
import io.lemonjuice.flandre_bot_framework.event.meta.BotStopEvent;
import io.lemonjuice.flandre_bot_framework.handler.ReceivingMessageHandler;
import io.lemonjuice.flandre_bot_framework.network.NetworkContainer;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Stop implements Runnable {

    @Override
    public void run() {
        BotConsole.close();
        log.info("正在停止应用...");
        BotEventBus.post(new BotStopEvent());
        ReceivingMessageHandler.stop();
        NetworkContainer.close();
    }
}
