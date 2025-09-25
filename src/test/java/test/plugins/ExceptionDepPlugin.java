package test.plugins;

import io.lemonjuice.flandre_bot_framework.plugins.BotPlugin;
import io.lemonjuice.flandre_bot_framework.plugins.PluginLoadingException;

import java.util.List;

public class ExceptionDepPlugin implements BotPlugin {
    @Override
    public String getName() {
        return "Exception Dependency";
    }

    @Override
    public List<Class<? extends BotPlugin>> getDependencies() {
        return List.of(RootDependencyPlugin.class);
    }

    @Override
    public void load() {
        throw new PluginLoadingException("test exception");
    }
}
