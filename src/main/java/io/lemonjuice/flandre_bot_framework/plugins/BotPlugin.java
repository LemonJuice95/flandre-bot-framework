package io.lemonjuice.flandre_bot_framework.plugins;

import io.lemonjuice.flandre_bot_framework.FlandreBot;
import lombok.extern.log4j.Log4j2;
import org.osgi.annotation.versioning.Version;

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
    default public List<PluginDependency> getDependencies() {
        return List.of();
    }

    /**
     * 标记插件的版本
     */
    default public PluginDependency.Version getVersion() {
        if(this.getClass().isAnnotationPresent(PluginVersion.class)) {
            PluginVersion version = this.getClass().getAnnotation(PluginVersion.class);
            return PluginDependency.Version.parseVersion(version.value());
        }
        return PluginDependency.Version.ANY;
    }

    /**
     * 初始化插件所需的配置文件
     * @return 配置文件是否无缺失
     */
    default public boolean initConfig() {
        return true;
    }

    /**
     * 执行插件加载逻辑
     */
    public void load();
}
