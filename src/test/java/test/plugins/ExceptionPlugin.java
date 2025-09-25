package test.plugins;

import io.lemonjuice.flandre_bot_framework.plugins.BotPlugin;

import java.util.List;

public class ExceptionPlugin implements BotPlugin {
    @Override
    public String getName() {
        return "Exception Plugin";
    }

    @Override
    public List<Class<? extends BotPlugin>> getDependencies() {
        return List.of(ExceptionDepPlugin.class);
    }

    @Override
    public void load() {
    }
}
