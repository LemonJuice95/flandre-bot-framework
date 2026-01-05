package io.lemonjuice.flandre_bot_framework.message.segment;

import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 音乐分享消息段
 */
@Getter
public class MusicMessageSegment extends MessageSegment {
    private final Type type;
    private final long id;
    private final String url;
    private final String audio;
    private final String title;

    public MusicMessageSegment(Type type, long id) {
        super("music");
        this.type = type;
        this.id = id;
        this.url = "";
        this.audio = "";
        this.title = "";
    }

    public MusicMessageSegment(String url, String audio, String title) {
        super("music");
        this.type = Type.CUSTOM;
        this.id = -1;
        this.url = url;
        this.audio = audio;
        this.title = title;
    }

    public MusicMessageSegment(JSONObject msgData) {
        super("music");
        this.type = Type.fromString(msgData.optString("type", ""));
        long id;
        try {
            id = Integer.parseInt(msgData.getString("id"));
        } catch (JSONException | NumberFormatException e) {
            id = -1;
        }
        this.id = id;
        this.url = msgData.optString("url", "");
        this.audio = msgData.optString("audio", "");
        this.title = msgData.optString("title", "");
    }

    @Override
    public String toString() {
        return "[音乐分享]";
    }

    @Override
    public JSONObject serializeMsgData() {
        JSONObject data = new JSONObject();
        data.put("type", this.type.toString());
        if(this.type == Type.CUSTOM) {
            data.put("url", this.url);
            data.put("audio", this.audio);
            data.put("title", this.title);
        } else {
            data.put("id", String.valueOf(this.id));
        }
        return data;
    }

    @Getter
    public enum Type {
        QQ("qq"),
        NETEASE("163"),
        XIAMI("xm"),
        CUSTOM("custom");

        private final String name;

        private Type(String name) {
            this.name = name;
        }

        public static Type fromString(String name) {
            return switch (name) {
                case "qq" -> QQ;
                case "163" -> NETEASE;
                case "xm" -> XIAMI;
                default -> CUSTOM;
            };
        }
    }
}
