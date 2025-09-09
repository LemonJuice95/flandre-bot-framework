package io.lemonjuice.flandre_bot;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

@Log4j2
public class FrameworkInfo {
    public static final String logo =
            """
                      _____ _                 _         \s
                     |  ___| | __ _ _ __   __| |_ __ ___\s
                     | |_  | |/ _` | '_ \\ / _` | '__/ _ \\
                     |  _| | | (_| | | | | (_| | | |  __/
                     |_|   |_|\\__,_|_| |_|\\__,_|_|  \\___|\
                    """;

    @Getter
    private static FrameworkInfo instance;

    public final Properties properties = new Properties();
    public final String version;

    private FrameworkInfo() {
        try (InputStream input = FlandreBot.class.getClassLoader().getResourceAsStream("framework.properties")) {
            this.properties.load(input);
        } catch (IOException e) {
            log.error("框架信息获取失败", e);
        }
        this.version = Optional.ofNullable(this.properties.getProperty("framework.version")).orElse("UNKNOWN");
    }



    static void init() {
        instance = new FrameworkInfo();
    }
}
