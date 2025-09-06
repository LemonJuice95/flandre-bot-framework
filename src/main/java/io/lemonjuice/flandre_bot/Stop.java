package io.lemonjuice.flandre_bot;

import io.lemonjuice.flandre_bot.event.BotEventBus;
import io.lemonjuice.flandre_bot.event.meta.BotStopEvent;
import io.lemonjuice.flandre_bot.network.WSClientCore;

public class Stop implements Runnable {
    @Override
    public void run() {
        BotEventBus.post(new BotStopEvent());
        WSClientCore.close();
    }
}
