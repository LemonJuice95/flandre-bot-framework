package io.lemonjuice.flandre_bot_framework.event.meta;

import io.lemonjuice.flandre_bot_framework.event.Event;
import io.lemonjuice.flandre_bot_framework.plugins.BotPlugin;
import io.lemonjuice.flandre_bot_framework.plugins.PluginsLoadingProcessor;
import lombok.AllArgsConstructor;

/**
 * 推送时机: 插件开始加载前<br>
 * 用于插件开发时注册插件自身
 */
@AllArgsConstructor
public class PluginRegisterEvent extends Event {
    private final PluginsLoadingProcessor loadingProcessor;

    public void register(BotPlugin plugin) {
        loadingProcessor.registerPlugin(plugin);
    }
}
