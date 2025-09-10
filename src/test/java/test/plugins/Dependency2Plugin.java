package test.plugins;

import io.lemonjuice.flandre_bot_framework.plugins.BotPlugin;

import java.util.List;

public class Dependency2Plugin implements BotPlugin {
    @Override
    public String getName() {
        return "Dependency 2";
    }

    @Override
    public void load() {

    }

    @Override
    public List<Class<? extends BotPlugin>> getDependencies() {
        return List.of();
    }
}
