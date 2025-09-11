package io.lemonjuice.flandre_bot_framework.console;

import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Function;

public class ConsoleListener implements Runnable {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        System.out.println("控制台命令系统已启动, 使用 'help' 或 '?' 查看命令列表");

        while(true) {
            String command = scanner.nextLine();
            String[] commandArray = Arrays.stream(command.split(" ")).filter(str -> !str.isEmpty()).toArray(String[]::new);
            String commandBody = commandArray[0];
            if(ConsoleCommandLookup.CONSOLE_COMMANDS.containsKey(commandBody)) {
                String[] args = Arrays.copyOfRange(commandArray, 1, commandArray.length);
                Function<String[], ConsoleCommandRunner> provider = ConsoleCommandLookup.CONSOLE_COMMANDS.get(commandBody);
                ConsoleCommandRunner runner = provider.apply(args);
                runner.apply();
            } else {
                System.out.println("未知命令, 使用 'help' 或 '?' 查看命令列表");
            }
        }
    }
}
