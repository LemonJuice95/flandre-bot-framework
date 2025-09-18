package io.lemonjuice.flandre_bot_framework.network;

import io.lemonjuice.flandre_bot_framework.config.BotBasicConfig;
import lombok.Getter;

public class NetworkContainer {
    @Getter
    private static INetworkImpl impl;

    //TODO 其他实现完成后添加进来
    public synchronized static void init(NetworkMode mode) {
        impl = WSClient.getInstance();
        if(!impl.connect(BotBasicConfig.NETWORK_URL.get(), BotBasicConfig.NETWORK_TOKEN.get())) {
            Thread.startVirtualThread(new WSReconnect());
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
