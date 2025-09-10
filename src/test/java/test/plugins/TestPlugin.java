package test.plugins;

import io.lemonjuice.flandre_bot_framework.plugins.BotPlugin;

import java.util.List;

public class TestPlugin implements BotPlugin {
    @Override
    public void load() {
        System.out.println("正常插件加载完毕！");
    }

    @Override
    public String getName() {
        return "Normal Plugin";
    }

    @Override
    public List<Class<? extends BotPlugin>> getDependencies() {
        return List.of(RootDependencyPlugin.class);
    }
}
