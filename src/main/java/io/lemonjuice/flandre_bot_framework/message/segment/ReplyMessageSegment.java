package io.lemonjuice.flandre_bot_framework.message.segment;

import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 回复消息段
 */
@Getter
public class ReplyMessageSegment extends MessageSegment {
    private final long msgId;

    public ReplyMessageSegment(long msgId) {
        super("reply");
        this.msgId = msgId;
    }

    public ReplyMessageSegment(JSONObject msgData) {
        super("reply");
        long msgId;
        try {
            msgId = Long.parseLong(msgData.getString("id"));
        } catch (NumberFormatException | JSONException e) {
            msgId = -1;
        }
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        return String.format("[回复消息:%d]", this.msgId);
    }

    @Override
    public JSONObject serializeMsgData() {
        JSONObject data = new JSONObject();
        data.put("id", String.valueOf(this.msgId));
        return data;
    }
}
