package test.plugins;

import io.lemonjuice.flandre_bot_framework.plugins.BotPlugin;

import java.util.List;

public class TestPlugin2 implements BotPlugin {
    @Override
    public String getName() {
        return "Normal Plugin 2";
    }

    @Override
    public List<Class<? extends BotPlugin>> getDependencies() {
        return List.of(DependencyPlugin.class, Dependency2Plugin.class);
    }

    @Override
    public void load() {
        System.out.println("插件2已加载");
    }
}
