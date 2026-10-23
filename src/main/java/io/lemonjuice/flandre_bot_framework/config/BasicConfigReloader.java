package io.lemonjuice.flandre_bot_framework.config;

import io.lemonjuice.flandre_bot_framework.command.BotCommandLookup;
import io.lemonjuice.flandre_bot_framework.event.BotEventBus;
import io.lemonjuice.flandre_bot_framework.event.annotation.SubscribeEvent;
import io.lemonjuice.flandre_bot_framework.event.meta.ConfigReloadEvent;
import io.lemonjuice.flandre_bot_framework.handler.ReceivingMessageHandler;
import io.lemonjuice.flandre_bot_framework.network.NetworkContainer;

public class BasicConfigReloader {
    @SubscribeEvent
    public void onConfigReload(ConfigReloadEvent.Post event) {
        if(event.getConfig() == BotBasicConfig.CONFIG) {
            ReceivingMessageHandler.reload();
            NetworkContainer.reload();
            BotEventBus.reload();
            BotCommandLookup.reloadConfig();
        }
    }
}
