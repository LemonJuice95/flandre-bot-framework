package io.lemonjuice.flandre_bot_framework.message.segment;

import org.json.JSONObject;

public abstract class MessageSegment {
    private final String segType;

    public MessageSegment(String segType) {
        this.segType = segType;
    }

    public JSONObject serialize() {
        JSONObject json = new JSONObject();
        json.put("type", this.segType);
        json.put("data", this.serializeMsgData());
        return json;
    }

    public abstract JSONObject serializeMsgData();
}
