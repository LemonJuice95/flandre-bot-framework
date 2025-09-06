package io.lemonjuice.flandre_bot.utils;

import io.lemonjuice.flandre_bot.config.BotBasicConfig;
import lombok.Getter;

import java.util.List;

public class DebugModeUtils {
    @Getter
    private static final boolean debugMode = BotBasicConfig.DEBUG_MODE.get();
    private static final List<Long> debugUsers = BotBasicConfig.DEBUG_USERS.get();

    public static boolean hasDebugPermission(long userId) {
        return debugMode && debugUsers.contains(userId);
    }
}
