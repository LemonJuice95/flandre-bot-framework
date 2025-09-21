package io.lemonjuice.flandre_bot_framework.config;

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

    public static final Supplier<String> BOT_NAME = () -> properties.getProperty("bot.name");
    public static final Supplier<Boolean> LOG_MESSAGES = () -> Boolean.parseBoolean(properties.getProperty("bot.log_messages"));

    public static final Supplier<String> NETWORK_URL = () -> properties.getProperty("bot.network.url");
    public static final Supplier<String> NETWORK_TOKEN = () -> properties.getProperty("bot.network.token");

    public static final Supplier<Boolean> DEBUG_MODE = () -> Boolean.parseBoolean(properties.getProperty("bot.debug_mode"));
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

    public static final Supplier<Boolean> COMMAND_SYNC_MODE = () -> Boolean.parseBoolean(properties.getProperty("bot.command_sync_mode"));

    public static void read() {
        try (InputStream input = new FileInputStream(configFile)) {
            properties.load(input);
        } catch (IOException e) {
            log.error("加载Bot配置文件失败！", e);
        }
    }
}
