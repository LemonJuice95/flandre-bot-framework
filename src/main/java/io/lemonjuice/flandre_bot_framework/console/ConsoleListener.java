package io.lemonjuice.flandre_bot_framework.console;

import io.lemonjuice.flandre_bot_framework.FlandreBot;
import lombok.extern.log4j.Log4j2;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;

import java.util.Arrays;
import java.util.function.Function;

@Log4j2
public class ConsoleListener implements Runnable {
    @Override
    public void run() {
        try {
            LineReader lineReader = BotConsole.getInstance().getLineReader();
            lineReader.printAbove("控制台命令系统已启动, 使用 'help' 或 '?' 查看命令列表");

            while (true) {
                String command = "";
                try {
                    command = lineReader.readLine("> ");
                } catch (EndOfFileException ignored) {
                    continue;
                }
                if(command.isEmpty()) {
                    continue;
                }

                String[] commandArray = Arrays.stream(command.split(" ")).filter(str -> !str.isEmpty()).toArray(String[]::new);
                String commandBody = commandArray[0];
                if (ConsoleCommandLookup.CONSOLE_COMMANDS.containsKey(commandBody)) {
                    String[] args = Arrays.copyOfRange(commandArray, 1, commandArray.length);
                    Function<String[], ConsoleCommandRunner> provider = ConsoleCommandLookup.CONSOLE_COMMANDS.get(commandBody);
                    ConsoleCommandRunner runner = provider.apply(args);
                    runner.apply();
                } else {
                    lineReader.printAbove("未知命令, 使用 'help' 或 '?' 查看命令列表");
                }
            }
        } catch (UserInterruptException e) {
            FlandreBot.stop();
        }
    }
}
