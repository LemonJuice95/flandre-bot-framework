package io.lemonjuice.flandre_bot_framework.console.original;

import io.lemonjuice.flandre_bot_framework.FlandreBot;
import io.lemonjuice.flandre_bot_framework.console.ConsoleCommandRunner;

import java.util.List;

public class ConsoleStopCommand extends ConsoleCommandRunner {
    public ConsoleStopCommand(String[] args) {
        super(args);
    }

    @Override
    public void apply() {
        FlandreBot.stop();
    }

    @Override
    public List<String> getCommandBodies() {
        return List.of("stop", "exit");
    }

    @Override
    public String getUsingFormat() {
        return "'stop'";
    }

    @Override
    public String getDescription() {
        return "停止应用运行";
    }
}
