package io.lemonjuice.flandre_bot.command;

import io.lemonjuice.flandre_bot.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BotCommandLookup {
    public static final List<Function<Message, CommandRunner>> GROUP_COMMANDS = new ArrayList<>();
    public static final List<Function<Message, CommandRunner>> PRIVATE_COMMANDS = new ArrayList<>();
    public static final List<Function<Message, CommandRunner>> FRIEND_COMMANDS = new ArrayList<>();
}
