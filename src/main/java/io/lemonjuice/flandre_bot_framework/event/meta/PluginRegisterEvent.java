package io.lemonjuice.flandre_bot_framework.event.meta;

import io.lemonjuice.flandre_bot_framework.event.Event;
import io.lemonjuice.flandre_bot_framework.plugins.BotPlugin;
import io.lemonjuice.flandre_bot_framework.plugins.PluginsLoadingProcessor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PluginRegisterEvent extends Event {
    private final PluginsLoadingProcessor loadingProcessor;

    public void register(BotPlugin plugin) {
        loadingProcessor.registerPlugin(plugin);
    }
}
