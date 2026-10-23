package io.lemonjuice.flandre_bot_framework.network;

import io.lemonjuice.flandre_bot_framework.FlandreBot;
import io.lemonjuice.flandre_bot_framework.config.BasicConfigReloader;
import io.lemonjuice.flandre_bot_framework.config.BotBasicConfig;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class NetworkContainer {
    @Getter
    private static volatile INetworkImpl impl;

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

    public static void reload() {
        boolean shouldReload = BotBasicConfig.NETWORK_MODE.changed();
        NetworkMode mode = BotBasicConfig.NETWORK_MODE.get();
        switch (mode) {
            case WS_CLIENT -> shouldReload |= BotBasicConfig.CLIENT_URL.changed();
            case WS_SERVER -> shouldReload |= BotBasicConfig.SERVER_HOST.changed() ||
                    BotBasicConfig.SERVER_PORT.changed();
            default -> {
                log.warn("配置重载时发现未知或不支持的网络连接模式，新的网络配置将被忽略");
                return;
            }
        }
        shouldReload |= BotBasicConfig.NETWORK_TOKEN.changed();
        if(!shouldReload) {
            return;
        }

        synchronized (NetworkContainer.class) {
            impl.close();
            switch (mode) {
                case WS_CLIENT -> {
                    impl = WSClient.getInstance();
                    if (!impl.init(BotBasicConfig.NETWORK_TOKEN.get())) {
                        Thread.startVirtualThread(new WSReconnect());
                    }
                }
                case WS_SERVER -> {
                    impl = WSServerContainer.getInstance();
                    impl.init(BotBasicConfig.NETWORK_TOKEN.get());
                }
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
