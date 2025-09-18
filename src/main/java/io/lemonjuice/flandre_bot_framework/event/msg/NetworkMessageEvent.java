package io.lemonjuice.flandre_bot_framework.event.msg;

import io.lemonjuice.flandre_bot_framework.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONObject;

/**
 * 为开发者保留一个入口来手动处理收到的原始信息
 */
@Getter
@AllArgsConstructor
public class NetworkMessageEvent extends Event {
    private final JSONObject message;
}