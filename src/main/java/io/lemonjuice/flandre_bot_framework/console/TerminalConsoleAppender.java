package io.lemonjuice.flandre_bot_framework.console;

import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;

@Plugin(name = "TerminalConsole", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class TerminalConsoleAppender extends AbstractAppender {

    protected TerminalConsoleAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreException, Property[] properties) {
        super(name, filter, layout, ignoreException, properties);
        BotConsole.init();
    }
    
    @PluginFactory
    public static TerminalConsoleAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
            @PluginElement("property") Property[] properties) {
        return new TerminalConsoleAppender(name, filter, layout != null ? layout : PatternLayout.createDefaultLayout(), ignoreExceptions, properties);
    }
    
    @Override
    public void append(LogEvent event) {
        String text = this.getLayout().toSerializable(event).toString();
        BotConsole.print(text);
    }

    @PluginBuilderFactory
    public static <B extends Builder<B>> B newBuilder() {
        return new Builder<B>().asBuilder();
    }

    public static class Builder<B extends Builder<B>> extends AbstractAppender.Builder<B>
            implements org.apache.logging.log4j.core.util.Builder<TerminalConsoleAppender> {

        @Override
        public TerminalConsoleAppender build() {
            return new TerminalConsoleAppender(getName(), getFilter(), getOrCreateLayout(),
                    isIgnoreExceptions(), getPropertyArray());
        }
    }

}