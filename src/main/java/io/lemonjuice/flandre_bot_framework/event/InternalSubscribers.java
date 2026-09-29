package io.lemonjuice.flandre_bot_framework.event;

import io.lemonjuice.flandre_bot_framework.account.AccountInfoInit;

public class InternalSubscribers {
    public static void register() {
        register(new AccountInfoInit());
    }

    private static void register(Object object) {
        BotEventBus.register(object);
    }
}
