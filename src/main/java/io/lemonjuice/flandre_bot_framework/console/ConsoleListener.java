package io.lemonjuice.flandre_bot_framework.console;

import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.function.Function;

@Log4j2
public class ConsoleListener implements Runnable {
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    @Override
    public void run() {
        System.out.println("控制台命令系统已启动, 使用 'help' 或 '?' 查看命令列表");

        while(true) {
            try {
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
            } catch (IOException e) {
                log.error("获取命令输入失败！", e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
