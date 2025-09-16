package io.lemonjuice.flandre_bot_framework.console;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ConsoleCommandRegister {
    private final List<Function<String[], ConsoleCommandRunner>> commands = new ArrayList<>();

    public void register(Function<String[], ConsoleCommandRunner> runnerProvider) {
        this.commands.add(runnerProvider);
    }

    public void load() {
        this.commands.forEach(provider -> {
            ConsoleCommandRunner tmpRunner = provider.apply(new String[]{});
            ConsoleCommandLookup.COMMAND_HELPS.add(String.format("%-45s - %s", tmpRunner.getUsingFormat(), tmpRunner.getDescription()));
            tmpRunner.getCommandBodies().forEach(body -> {
                ConsoleCommandLookup.CONSOLE_COMMANDS.put(body, provider);
            });
        });
    }
}
