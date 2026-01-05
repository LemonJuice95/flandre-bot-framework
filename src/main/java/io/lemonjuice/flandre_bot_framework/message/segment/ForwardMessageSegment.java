package io.lemonjuice.flandre_bot_framework.message.segment;

import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 合并转发消息段
 */
@Getter
public class ForwardMessageSegment extends MessageSegment {
    private final long forwardId;

    public ForwardMessageSegment(long forwardId) {
        super("forward");
        this.forwardId = forwardId;
    }

    public ForwardMessageSegment(JSONObject msgData) {
        super("forward");
        long forwardId;
        try {
            forwardId = Long.parseLong(msgData.getString("id"));
        } catch (JSONException | NumberFormatException e) {
            forwardId = -1;
        }
        this.forwardId = forwardId;
    }

    @Override
    public String toString() {
        return String.format("[合并转发消息:%d]", this.forwardId);
    }

    @Override
    public JSONObject serializeMsgData() {
        JSONObject data = new JSONObject();
        data.put("id", String.valueOf(this.forwardId));
        return data;
    }
}
