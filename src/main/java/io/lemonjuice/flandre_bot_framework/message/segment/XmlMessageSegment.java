package io.lemonjuice.flandre_bot_framework.message.segment;

import lombok.Getter;
import org.json.JSONObject;

/**
 * xml消息段
 */
@Getter
public class XmlMessageSegment extends MessageSegment {
    private final String data;

    public XmlMessageSegment(String data) {
        super("xml");
        this.data = data;
    }

    public XmlMessageSegment(JSONObject msgData) {
        this(msgData.optString("data", ""));
    }

    @Override
    public String toString() {
        return "[xml消息]";
    }

    @Override
    public JSONObject serializeMsgData() {
        JSONObject data = new JSONObject();
        data.put("data", this.data);
        return data;
    }
}
