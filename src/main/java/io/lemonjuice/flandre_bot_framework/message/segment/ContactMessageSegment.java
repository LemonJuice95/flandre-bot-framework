package io.lemonjuice.flandre_bot_framework.message.segment;

import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 群聊/好友推荐消息段
 */
@Getter
public class ContactMessageSegment extends MessageSegment {
    private final Type type;
    private final long id;

    public ContactMessageSegment(Type type, long id) {
        super("contact");
        this.type = type;
        this.id = id;
    }

    public ContactMessageSegment(JSONObject msgData) {
        super("contact");
        this.type = Type.fromString(msgData.optString("type", "unknown"));
        long id;
        try {
            id = Long.parseLong(msgData.getString("id"));
        } catch (JSONException | NumberFormatException e) {
            id = -1L;
        }
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("[%s推荐:%d]", this.type.getMsg(), this.id);
    }

    @Override
    public JSONObject serializeMsgData() {
        JSONObject data = new JSONObject();
        data.put("type", this.type.toString().toLowerCase());
        data.put("qq", String.valueOf(this.id));
        return data;
    }

    @Getter
    public enum Type {
        QQ("好友"),
        GROUP("群聊"),
        UNKNOWN("未知类型");

        private final String msg;

        private Type(String msg) {
            this.msg = msg;
        }

        public static Type fromString(String name) {
            return Type.valueOf(name.toUpperCase());
        }
    }
}
