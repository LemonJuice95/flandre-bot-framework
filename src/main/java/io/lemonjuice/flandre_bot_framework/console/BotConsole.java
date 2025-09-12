package io.lemonjuice.flandre_bot_framework.console;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

@Log4j2
public class BotConsole {
    @Getter
    private static volatile BotConsole instance;
    @Getter
    private static volatile boolean available = false;

    @Getter
    private final Terminal terminal;
    @Getter
    private final LineReader lineReader;

    private BotConsole() throws IOException {
        this.terminal = TerminalBuilder.builder().dumb(true).build();
        this.lineReader = LineReaderBuilder.builder().terminal(this.terminal).build();
    }

    public synchronized static void init() {
        try {
            instance = new BotConsole();
            available = true;
        } catch (IOException e) {
            log.error("创建JLine Terminal失败！控制台命令系统将禁用", e);
        }
    }

    public synchronized static void close() {
        try {
            instance.terminal.close();
        } catch (IOException | NullPointerException ignored) {
        } finally {
            available = false;
            instance = null;
        }
    }
}
