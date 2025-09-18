package io.lemonjuice.flandre_bot_framework.account;

import io.lemonjuice.flandre_bot_framework.event.annotation.EventSubscriber;
import io.lemonjuice.flandre_bot_framework.event.annotation.SubscribeEvent;
import io.lemonjuice.flandre_bot_framework.event.meta.NetworkConnectedEvent;
import lombok.extern.log4j.Log4j2;

@EventSubscriber
@Log4j2
public class AccountInfoInit {
    @SubscribeEvent
    public void init(NetworkConnectedEvent event) {
        log.info("正在获取Bot账号内部分信息，初始化完成前群聊名称等相关内容可能无法正常使用");
        Thread.startVirtualThread(AccountInfoInit::init);
    }

    public static void init() {
        AccountInfo.init();
        ContextManager.init();
    }
}
