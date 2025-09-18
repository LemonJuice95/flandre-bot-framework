package io.lemonjuice.flandre_bot_framework.network;

import lombok.Getter;

@Getter
public enum NetworkMode {
    WS_CLIENT("Websocket正向连接"),
    WS_SERVER("Websocket反向连接"),
    HTTP_CLIENT("Http正向连接");

    private final String name;

    private NetworkMode(String name) {
        this.name = name;
    }
}
