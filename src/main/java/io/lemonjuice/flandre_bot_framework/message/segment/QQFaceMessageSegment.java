package io.lemonjuice.flandre_bot_framework.message.segment;

import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * QQ表情消息段，
 * 关于表情id列表，参见 <a href="https://github.com/kyubotics/coolq-http-api/wiki/%E8%A1%A8%E6%83%85-CQ-%E7%A0%81-ID-%E8%A1%A8">表情id一览</a>
 */
@Getter
public class QQFaceMessageSegment extends MessageSegment {
    private final int faceId;

    public QQFaceMessageSegment(int faceId) {
        super("face");
        this.faceId = faceId;
    }

    public QQFaceMessageSegment(JSONObject msgData) {
        super("face");
        int id = -1;
        try {
            id = Integer.parseInt(msgData.getString("id"));
        } catch (JSONException | NumberFormatException e) {
            id = -1;
        }
        this.faceId = id;
    }

    @Override
    public String toString() {
        return String.format("[QQ表情:%d]", this.faceId);
    }

    @Override
    public JSONObject serializeMsgData() {
        JSONObject data = new JSONObject();
        data.put("id", data);
        return data;
    }
}
