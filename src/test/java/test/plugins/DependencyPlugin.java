package test.plugins;

import io.lemonjuice.flandre_bot_framework.plugins.BotPlugin;

import java.util.List;

public class DependencyPlugin implements BotPlugin {
    @Override
    public List<Class<? extends BotPlugin>> getDependencies() {
        return List.of(RootDependencyPlugin.class);
    }

    @Override
    public void load() {
    }

    @Override
    public String getName() {
        return "Dependency";
    }
}
