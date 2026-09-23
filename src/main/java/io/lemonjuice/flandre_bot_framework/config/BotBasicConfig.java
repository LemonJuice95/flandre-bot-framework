package io.lemonjuice.flandre_bot_framework.config;

import io.lemonjuice.flandre_bot_framework.FlandreBot;
import io.lemonjuice.flandre_bot_framework.event.bus.BusMode;
import io.lemonjuice.flandre_bot_framework.network.NetworkMode;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

@Log4j2
public class BotBasicConfig {
    public static final File configFile = new File("./config/bot.properties");
    public static final File defaultConfigFile = new File("config/bot.properties");

    public static final BotConfig CONFIG = FlandreBot.registerConfig(new BotConfig(configFile, defaultConfigFile)
            .failWhenExport()
            .name("basic")
            .description("Bot的基础配置"));

    public static final ConfigItem<Boolean> LOG_MESSAGES = CONFIG.register(CONFIG::getBoolean, "bot.log_messages", false);

    public static final ConfigItem<NetworkMode> NETWORK_MODE = CONFIG.register(() -> {
        try {
            int index = CONFIG.getInt("bot.network.mode", -1);
            return NetworkMode.values()[index];
        } catch (IndexOutOfBoundsException e) {
            return NetworkMode.UNKNOWN;
        }
    });
    public static final ConfigItem<String> CLIENT_URL = CONFIG.register(CONFIG::getString, "bot.network.client.url", "");
    public static final ConfigItem<String> SERVER_HOST = CONFIG.register(CONFIG::getString,"bot.network.server.host", "");
    public static final ConfigItem<Integer> SERVER_PORT = CONFIG.register(CONFIG::getInt, "bot.network.server.port", 49500);
    public static final ConfigItem<String> NETWORK_TOKEN = CONFIG.register(CONFIG::getString, "bot.network.token", "");

    public static final ConfigItem<Boolean> DEBUG_MODE = CONFIG.register(CONFIG::getBoolean, "bot.debug_mode", false);
    public static final ConfigItem<List<Long>> DEBUG_USERS = CONFIG.register(CONFIG::getLongList, "bot.debug_users");

    public static final ConfigItem<BusMode> EVENT_BUS_MODE = CONFIG.register(CONFIG::getEnum, "bot.eventbus.bus_mode", BusMode.SYNC);

    public static final ConfigItem<Boolean> COMMAND_SYNC_MODE = CONFIG.register(CONFIG::getBoolean, "bot.command_sync_mode", false);

    public static void init() {
        if(!CONFIG.load() && CONFIG.isExportedOnLastLoad()) {
            FlandreBot.markKeyConfigLost("请先在config/bot.properties文件内进行配置后再次启动");
        }
    }
}
