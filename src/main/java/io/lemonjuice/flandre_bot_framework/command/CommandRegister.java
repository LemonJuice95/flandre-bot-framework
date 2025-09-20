package io.lemonjuice.flandre_bot_framework.command;

import io.lemonjuice.flandre_bot_framework.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CommandRegister {
    private final List<Function<Message, CommandRunner>> commands = new ArrayList<>();

    public void register(Function<Message, CommandRunner> commandRunner) {
        this.commands.add(commandRunner);
    }

    public void load() {
        this.commands.forEach((c) -> {
            getDestination(c.apply(Message.DUMMY).getType()).forEach(l -> {
                l.add(c);
            });
        });
    }

    private static List<List<Function<Message, CommandRunner>>> getDestination(CommandRunner.Type type) {
        return switch (type) {
            case GROUP -> List.of(BotCommandLookup.GROUP_COMMANDS);
            case PRIVATE -> List.of(BotCommandLookup.PRIVATE_COMMANDS);
            case FRIEND -> List.of(BotCommandLookup.FRIEND_COMMANDS);
            case GENERAL -> List.of(BotCommandLookup.GROUP_COMMANDS, BotCommandLookup.PRIVATE_COMMANDS);
            default -> throw new IllegalStateException("未知的命令类型: " + type);
        };
    }
}
