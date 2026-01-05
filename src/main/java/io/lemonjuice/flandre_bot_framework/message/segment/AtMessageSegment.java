package io.lemonjuice.flandre_bot_framework.message.segment;

import lombok.Getter;
import org.json.JSONObject;

/**
 * [@某人]的消息段
 */
public class AtMessageSegment extends MessageSegment {
    private final long qq;
    @Getter
    private final boolean all;

    public AtMessageSegment(long qq, boolean all) {
       super("at");
       this.qq = qq;
       this.all = all;
    }

    public AtMessageSegment(long qq) {
        this(qq, false);
    }

    public static AtMessageSegment atAll() {
        return new AtMessageSegment(-1L, true);
    }

    public AtMessageSegment(JSONObject msgData) {
        super("at");
        String at = msgData.optString("qq", "");
        if(at.equals("all")) {
            this.qq = -1L;
            this.all = true;
        } else {
            long id;
            try {
                id = Long.parseLong(at);
            } catch (NumberFormatException e) {
                id = -1L;
            }
            this.qq = id;
            this.all = false;
        }
    }

    public long getQQ() {
        return this.qq;
    }

    @Override
    public String toString() {
        return String.format("[@%s]", this.all ? "全体成员" : String.valueOf(this.qq));
    }

    @Override
    public JSONObject serializeMsgData() {
        JSONObject data = new JSONObject();
        if(this.all) {
            data.put("qq", "all");
        } else {
            data.put("qq", String.valueOf(this.qq));
        }
        return data;
    }
}
