package io.lemonjuice.flandre_bot_framework.network;

import io.lemonjuice.flandre_bot_framework.FlandreBot;
import io.lemonjuice.flandre_bot_framework.config.BotBasicConfig;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class NetworkContainer {
    @Getter
    private static INetworkImpl impl;

    //TODO 其他实现完成后添加进来
    public synchronized static void init(NetworkMode mode) {
        log.info("初始化网络模块...");
        log.info("连接模式: {}", mode.getName());
        switch (mode) {
            case NetworkMode.WS_CLIENT -> {
                impl = WSClient.getInstance();
                if (!impl.init(BotBasicConfig.NETWORK_TOKEN.get())) {
                    Thread.startVirtualThread(new WSReconnect());
                }
            }
            case NetworkMode.WS_SERVER -> {
                impl = WSServerContainer.getInstance();
                impl.init(BotBasicConfig.NETWORK_TOKEN.get());
            }
            default -> {
                log.fatal("未知或不支持的连接模式，应用无法启动");
                FlandreBot.stop();
            }
        }

    }

    public synchronized static void close() {
        try {
            impl.close();
        } catch (NullPointerException ignored) {
            //说明连接不存在，无需处理
        }
    }
}
