package io.lemonjuice.flandre_bot_framework.event.meta;

import io.lemonjuice.flandre_bot_framework.event.Event;

/**
 * @deprecated 缺失依赖关系处理逻辑
 * 目前该事件已不会被推送
 * 开发插件时，请将插件主类继承{@link io.lemonjuice.flandre_bot_framework.plugins.BotPlugin}
 * 并订阅{@link PluginRegisterEvent}事件来注册插件
 */
@Deprecated(forRemoval = true)
public class PluginLoadEvent extends Event {
}
