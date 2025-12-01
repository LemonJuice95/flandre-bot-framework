package io.lemonjuice.flandre_bot_framework.config;

import io.lemonjuice.flandre_bot_framework.FlandreBot;
import lombok.extern.log4j.Log4j2;

import java.io.*;

@Log4j2
public class BasicConfigFileInit {
    public static void init() {
        log.info("正在检查配置文件");
        checkExists();
        BotBasicConfig.read();
    }

    private static void checkExists() {
        if(!BotBasicConfig.configFile.getParentFile().exists()) {
            BotBasicConfig.configFile.getParentFile().mkdirs();
        }

        if(!BotBasicConfig.configFile.exists()) {
            log.warn("未发现配置文件，即将进行文件释放");
            try (InputStream input = BasicConfigFileInit.class.getClassLoader().getResourceAsStream("config/bot.properties");
                 OutputStream output = new FileOutputStream(BotBasicConfig.configFile)) {
                output.write(input.readAllBytes());
            } catch (Exception e) {
                log.error("配置文件释放失败！", e);
            }
            FlandreBot.markKeyConfigLost("请先在config/bot.properties文件内进行配置后再次启动");
        }
    }
}
