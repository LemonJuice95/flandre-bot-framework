package io.lemonjuice.flandre_bot_framework.console;

import lombok.extern.log4j.Log4j2;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

@Log4j2
public class ConsoleListener implements Runnable {
    @Override
    public void run() {
        try (Terminal terminal = TerminalBuilder.builder().system(true).build()) {
            LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();
            System.out.println("控制台命令系统已启动, 使用 'help' 或 '?' 查看命令列表");

            while (true) {
                String command = reader.readLine();
                String[] commandArray = Arrays.stream(command.split(" ")).filter(str -> !str.isEmpty()).toArray(String[]::new);
                String commandBody = commandArray[0];
                if (ConsoleCommandLookup.CONSOLE_COMMANDS.containsKey(commandBody)) {
                    String[] args = Arrays.copyOfRange(commandArray, 1, commandArray.length);
                    Function<String[], ConsoleCommandRunner> provider = ConsoleCommandLookup.CONSOLE_COMMANDS.get(commandBody);
                    ConsoleCommandRunner runner = provider.apply(args);
                    runner.apply();
                } else {
                    System.out.println("未知命令, 使用 'help' 或 '?' 查看命令列表");
                }
            }
        } catch (IOException e) {
            log.error("控制台命令系统启动失败！", e);
        }
    }
}
