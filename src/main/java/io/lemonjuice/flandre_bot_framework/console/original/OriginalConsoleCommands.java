package io.lemonjuice.flandre_bot_framework.console.original;


import io.lemonjuice.flandre_bot_framework.console.ConsoleCommandRegister;
import io.lemonjuice.flandre_bot_framework.console.ConsoleCommandRunner;

import java.util.function.Function;

public class OriginalConsoleCommands {
    public static final ConsoleCommandRegister ORIGINAL_CONSOLE_COMMANDS = new ConsoleCommandRegister();

    static {
        register(ConsoleHelpCommand::new);
        register(ConsoleStopCommand::new);
    }

    private static void register(Function<String[], ConsoleCommandRunner> runnerProvider) {
        ORIGINAL_CONSOLE_COMMANDS.register(runnerProvider);
    }
}
