package io.lemonjuice.flandre_bot_framework.message.segment;

import lombok.Getter;
import org.json.JSONObject;

/**
 * 最简单的消息段类型（纯文本）
 */
@Getter
public class TextMessageSegment extends MessageSegment {
    private final String content;

    public TextMessageSegment(String content) {
        super("text");
        this.content = content;
    }

    public TextMessageSegment(JSONObject msgData) {
        this(msgData.optString("text", ""));
    }

    @Override
    public String toString() {
        return this.content;
    }

    @Override
    public JSONObject serializeMsgData() {
        JSONObject data = new JSONObject();
        data.put("text", this.content);
        return data;
    }
}
