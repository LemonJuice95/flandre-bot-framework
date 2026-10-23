package io.lemonjuice.flandre_bot_framework.event;

import io.lemonjuice.flandre_bot_framework.account.AccountInfoInit;
import io.lemonjuice.flandre_bot_framework.config.BasicConfigReloader;

public class InternalSubscribers {
    public static void register() {
        register(new AccountInfoInit());
        register(new BasicConfigReloader());
    }

    private static void register(Object object) {
        BotEventBus.register(object);
    }
}
