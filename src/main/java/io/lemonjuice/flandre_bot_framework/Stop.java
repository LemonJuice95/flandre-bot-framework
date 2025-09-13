package io.lemonjuice.flandre_bot_framework;

import io.lemonjuice.flandre_bot_framework.console.BotConsole;
import io.lemonjuice.flandre_bot_framework.event.BotEventBus;
import io.lemonjuice.flandre_bot_framework.event.meta.BotStopEvent;
import io.lemonjuice.flandre_bot_framework.network.WSClientCore;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Stop implements Runnable {
    @Override
    public void run() {
        log.info("正在停止应用...");
        BotEventBus.post(new BotStopEvent());
        BotConsole.close();
        WSClientCore.close();
    }
}
