package io.lemonjuice.flandre_bot_framework.network;

import io.lemonjuice.flandre_bot_framework.config.BotBasicConfig;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class WSReconnect implements Runnable {
    private int waitTime = 5000;

    @Override
    public void run() {
        int retry_count = 0;
        while(true) {
            try {
                Thread.sleep(this.waitTime);
            } catch (InterruptedException e) {
                break;
            }
            retry_count++;
            log.info("正在尝试重连Bot，次数: {}", retry_count);
            if (WSClientCore.connect(BotBasicConfig.WS_URL.get(), BotBasicConfig.WS_TOKEN.get())) {
                log.info("重连成功! ");
                break;
            } else {
                log.info("重连失败，将在{}秒后重试", this.waitTime / 1000);
            }

            if(this.waitTime < 160000) {
                this.waitTime *= 2;
            }
        }
    }
}
