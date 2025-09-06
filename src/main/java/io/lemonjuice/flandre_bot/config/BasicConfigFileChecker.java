package io.lemonjuice.flandre_bot.config;

import io.lemonjuice.flandre_bot.FlandreBot;
import lombok.extern.log4j.Log4j2;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@Log4j2
public class BasicConfigFileChecker {
    public static void check() {
        log.info("正在检查配置文件是否存在");

        if(!BotBasicConfig.configFile.getParentFile().exists()) {
            BotBasicConfig.configFile.getParentFile().mkdirs();
        }

        if(!BotBasicConfig.configFile.exists()) {
            log.warn("未发现配置文件，即将进行文件释放");
            try (InputStream input = BasicConfigFileChecker.class.getClassLoader().getResourceAsStream("config/bot.properties");
                 OutputStream output = new FileOutputStream(BotBasicConfig.configFile)) {
                output.write(input.readAllBytes());
            } catch (Exception e) {
                log.error("配置文件释放失败！", e);
            }
            log.warn("请先在config/bot.properties文件内进行配置后再次启动");
            FlandreBot.stop();
        }
    }
}
