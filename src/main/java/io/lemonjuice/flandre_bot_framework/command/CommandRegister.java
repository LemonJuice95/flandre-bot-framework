package io.lemonjuice.flandre_bot_framework.command;

import io.lemonjuice.flandre_bot_framework.command.annotations.ForceNoPooling;
import io.lemonjuice.flandre_bot_framework.command.annotations.Resettable;
import io.lemonjuice.flandre_bot_framework.config.BotBasicConfig;
import io.lemonjuice.flandre_bot_framework.model.Message;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class CommandRegister {
    private final List<Function<Message, CommandRunner>> commands = new ArrayList<>();

    public void register(Function<Message, CommandRunner> commandRunner) {
        this.commands.add(commandRunner);
    }

    public void load() {
        this.commands.forEach((c) -> {
            CommandRunner runner = c.apply(Message.DUMMY);
            getDestination(runner.getType()).forEach(dest -> {
                CommandRunnerSlot slot = new CommandRunnerSlot(runner.getClass(), c);
                if(BotCommandLookup.SHOULD_POOLING.get(runner.getClass()) && BotBasicConfig.POOLING_COMMAND_RUNNERS.get()) {
                    slot.usePool(BotBasicConfig.RUNNER_POOL_INITIAL_CAPACITY.get(), BotBasicConfig.RUNNER_POOL_MAXIMUM_CAPACITY.get(), c);
                } else {
                    slot.useDummyPool();
                }
                dest.add(slot);
            });
        });
    }

    private static List<List<CommandRunnerSlot>> getDestination(CommandRunner.Type type) {
        return switch (type) {
            case GROUP -> List.of(BotCommandLookup.GROUP_COMMANDS);
            case PRIVATE -> List.of(BotCommandLookup.PRIVATE_COMMANDS);
            case FRIEND -> List.of(BotCommandLookup.FRIEND_COMMANDS);
            case GENERAL -> List.of(BotCommandLookup.GROUP_COMMANDS, BotCommandLookup.PRIVATE_COMMANDS);
            default -> throw new IllegalStateException("未知的命令类型: " + type);
        };
    }
}
