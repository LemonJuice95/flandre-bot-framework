package io.lemonjuice.flandre_bot_framework;

import io.lemonjuice.flandre_bot_framework.event.BotEventBus;
import io.lemonjuice.flandre_bot_framework.event.meta.BotStopEvent;
import io.lemonjuice.flandre_bot_framework.network.WSClientCore;

public class Stop implements Runnable {
    @Override
    public void run() {
        BotEventBus.post(new BotStopEvent());
        WSClientCore.close();
    }
}
