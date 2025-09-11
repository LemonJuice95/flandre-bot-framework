package io.lemonjuice.flandre_bot_framework.console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ConsoleCommandLookup {
    public static final Map<String, Function<String[], ConsoleCommandRunner>> CONSOLE_COMMANDS = new HashMap<>();
    public static final List<String> COMMAND_HELPS = new ArrayList<>();
}
