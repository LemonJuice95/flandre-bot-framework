package io.lemonjuice.flandre_bot_framework.message.segment;

import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * json消息段
 */
@Getter
public class JsonMessageSegment extends MessageSegment {
    private final JSONObject data;

    public JsonMessageSegment(JSONObject data) {
        super("json");
        this.data = data;
    }

    public static JsonMessageSegment deserializeMsgData(JSONObject msgData) {
        try {
            return new JsonMessageSegment(new JSONObject(msgData.getString("data")));
        } catch (JSONException e) {
            return new JsonMessageSegment(new JSONObject());
        }
    }

    @Override
    public String toString() {
        return "[json消息]";
    }

    @Override
    public JSONObject serializeMsgData() {
        JSONObject data = new JSONObject();
        data.put("data", this.data.toString());
        return data;
    }
}
