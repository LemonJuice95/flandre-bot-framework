package io.lemonjuice.flandre_bot_framework.plugins;

import java.util.List;

public interface BotPlugin {
    /**
     * 标记插件的名称
     * @return 插件名称
     */
    public String getName();

    /**
     * 标记哪些插件需要在此插件加载前被加载
     * @return 前置插件类列表
     */
    default public List<Class<? extends BotPlugin>> getDependencies() {
        return List.of();
    }

    /**
     * 执行插件加载逻辑
     */
    public void load();
}
