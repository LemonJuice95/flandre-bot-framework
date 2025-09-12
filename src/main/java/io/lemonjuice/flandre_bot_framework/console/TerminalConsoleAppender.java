package io.lemonjuice.flandre_bot_framework.console;

import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;

import java.io.PrintStream;
import java.io.Serializable;

@Plugin(name = "TerminalConsole", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class TerminalConsoleAppender extends AbstractAppender {
    private static Terminal terminal;
    private static LineReader lineReader;
    private static final PrintStream stdout = System.out;
    
    protected TerminalConsoleAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreException, Property[] properties) {
        super(name, filter, layout, ignoreException, properties);
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
    
    public synchronized static void bindConsole(BotConsole console) {
        terminal = console.getTerminal();
        lineReader = console.getLineReader();
    }
    
    @Override
    public void append(LogEvent event) {
        String text = this.getLayout().toSerializable(event).toString();
        if (terminal != null) {
            if (lineReader != null) {
                lineReader.printAbove(text);
            } else {
                terminal.writer().print(text);
                terminal.writer().flush();
            }
        } else {
            stdout.print(text);
        }
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