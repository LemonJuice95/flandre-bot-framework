package io.lemonjuice.flandre_bot_framework.utils;

import io.lemonjuice.flandre_bot_framework.config.BotBasicConfig;
import lombok.Getter;

import java.util.List;

public class DebugModeUtils {
    public static boolean isDebugMode() {
        return BotBasicConfig.DEBUG_MODE.get();
    }

    public static boolean hasDebugPermission(long userId) {
        return BotBasicConfig.DEBUG_MODE.get() && BotBasicConfig.DEBUG_USERS.get().contains(userId);
    }
}
