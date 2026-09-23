package io.lemonjuice.flandre_bot_framework.config;

import java.util.concurrent.ConcurrentHashMap;

public class ConfigLookup {
    public static final ConcurrentHashMap<String, BotConfig> CONFIGS = new ConcurrentHashMap<>();

    public static BotConfig register(BotConfig config) {
        if(config.getName() != null && !config.getName().isBlank())
            CONFIGS.put(config.getName(), config);
        return config;
    }
}
