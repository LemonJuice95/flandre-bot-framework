package test.plugins;

import io.lemonjuice.flandre_bot_framework.plugins.BotPlugin;

import java.util.List;

public class RootDependencyPlugin implements BotPlugin {
    @Override
    public String getName() {
        return "Root Dependency";
    }

    @Override
    public List<Class<? extends BotPlugin>> getDependencies() {
        return List.of();
    }

    @Override
    public void load() {
    }
}
