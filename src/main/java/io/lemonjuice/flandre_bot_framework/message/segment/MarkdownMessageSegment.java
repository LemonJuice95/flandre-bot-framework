package io.lemonjuice.flandre_bot_framework.message.segment;

import lombok.Getter;
import org.json.JSONObject;

@Getter
public class MarkdownMessageSegment extends MessageSegment {
    private final String content;

    public MarkdownMessageSegment(String content) {
        super("markdown");
        this.content = content;
    }

    public MarkdownMessageSegment(JSONObject msgData) {
        this(msgData.optString("content", ""));
    }

    @Override
    public String toString() {
        return this.content;
    }

    @Override
    public JSONObject serializeMsgData() {
        JSONObject data = new JSONObject();
        data.put("content", this.content);
        return data;
    }
}
