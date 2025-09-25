package test.plugins;

import io.lemonjuice.flandre_bot_framework.plugins.PluginsLoadingProcessor;
import org.junit.jupiter.api.Test;

public class PluginLoaderTest {
    @Test
    public void testPluginLoader() {
        PluginsLoadingProcessor processor = new PluginsLoadingProcessor();

        processor.registerPlugin(new DependencyPlugin());
        processor.registerPlugin(new RootDependencyPlugin());
        processor.registerPlugin(new TestPlugin());
        processor.registerPlugin(new Dependency2Plugin());
        processor.registerPlugin(new TestPlugin2());
        processor.registerPlugin(new ExceptionPlugin());
        processor.registerPlugin(new ExceptionDepPlugin());

        processor.loadPlugins();
    }
}
