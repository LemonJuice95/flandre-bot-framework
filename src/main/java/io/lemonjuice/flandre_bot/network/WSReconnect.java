package io.lemonjuice.flandre_bot.network;

import io.lemonjuice.flandre_bot.config.BotBasicConfig;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class WSReconnect implements Runnable {
    @Override
    public void run() {
        int retry_count = 0;
        while(true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                break;
            }
            retry_count++;
            log.info("正在尝试重连Bot，次数: {}", retry_count);
            if (WSClientCore.connect(BotBasicConfig.WS_URL.get(), BotBasicConfig.WS_TOKEN.get())) {
                log.info("重连成功! ");
                break;
            }
        }
    }
}
