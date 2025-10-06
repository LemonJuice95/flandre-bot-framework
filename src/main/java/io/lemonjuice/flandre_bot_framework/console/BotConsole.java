package io.lemonjuice.flandre_bot_framework.console;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
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
        this.lineReader = LineReaderBuilder.builder().terminal(this.terminal).parser(new DefaultParser().escapeChars(new char[0])).build();
    }

    public static String readLine() {
        return instance.lineReader.readLine();
    }

    public static String readLine(String s) {
        return instance.lineReader.readLine(s);
    }

    public static void println(String str) {
        if(!available || instance == null || instance.terminal == null) {
            System.out.println(str);
        } else {
            if(instance.lineReader != null) {
                instance.lineReader.printAbove(str);
            } else {
                instance.terminal.writer().println(str);
                instance.terminal.writer().flush();
            }
        }
    }

    public static void print(String str) {
        if(!available || instance == null || instance.terminal == null) {
            System.out.print(str);
        } else {
            if(instance.lineReader != null) {
                instance.lineReader.printAbove(str);
            } else {
                instance.terminal.writer().print(str);
                instance.terminal.writer().flush();
            }
        }
    }


    public synchronized static void init() {
        try {
            if(!available) {
                instance = new BotConsole();
                available = true;
            }
        } catch (IOException e) {
            log.error("创建JLine Terminal失败！控制台命令系统将禁用", e);
        }
    }

    public synchronized static void close() {
        if(available) {
            try {
                instance.terminal.close();
            } catch (IOException | NullPointerException ignored) {
            } finally {
                available = false;
                instance = null;
            }
        }
    }
}
