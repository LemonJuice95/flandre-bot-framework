package io.lemonjuice.flandre_bot_framework.config;

import io.lemonjuice.flandre_bot_framework.network.NetworkMode;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class BotBasicConfig {
    public static final File configFile = new File("./config/bot.properties");

    public static final Properties properties = new Properties();

    public static final Supplier<Boolean> LOG_MESSAGES = () -> Boolean.valueOf(properties.getProperty("bot.log_messages"));

    public static final Supplier<NetworkMode> NETWORK_MODE = () -> {
        try {
            int index = Integer.parseInt(properties.getProperty("bot.network.mode"));
            return NetworkMode.values()[index];
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return NetworkMode.UNKNOWN;
        }
    };
    public static final Supplier<String> CLIENT_URL = () -> properties.getProperty("bot.network.client.url");
    public static final Supplier<String> SERVER_HOST = () -> properties.getProperty("bot.network.server.host");
    public static final Supplier<Integer> SERVER_PORT = () -> {
        try {
            return Integer.valueOf(properties.getProperty("bot.network.server.port"));
        } catch (NumberFormatException e) {
            log.warn("服务器端口格式不正确，将使用默认端口49500");
            return 49500;
        }
    };
    public static final Supplier<String> NETWORK_TOKEN = () -> properties.getProperty("bot.network.token");

    public static final Supplier<Boolean> DEBUG_MODE = () -> Boolean.valueOf(properties.getProperty("bot.debug_mode"));
    public static final Supplier<List<Long>> DEBUG_USERS = () -> {
        List<Long> result = new ArrayList<>();
        String ids = properties.getProperty("bot.debug_users");

        Pattern prepare = Pattern.compile("\\{?[\\d\\s,]+}?");
        Matcher prepareMatcher = prepare.matcher(ids);
        ids = prepareMatcher.find() ? prepareMatcher.group() : "";

        Pattern userId = Pattern.compile("\\d+");
        Matcher idMatcher = userId.matcher(ids);
        while(idMatcher.find()) {
            result.add(Long.parseLong(idMatcher.group()));
        }
        return result;
    };

    public static final Supplier<Boolean> COMMAND_SYNC_MODE = () -> Boolean.valueOf(properties.getProperty("bot.command_sync_mode"));

    public static void read() {
        try (InputStream input = new FileInputStream(configFile)) {
            properties.load(input);
        } catch (IOException e) {
            log.error("加载Bot配置文件失败！", e);
        }
    }
}
