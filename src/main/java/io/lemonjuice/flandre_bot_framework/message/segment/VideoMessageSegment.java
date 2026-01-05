package io.lemonjuice.flandre_bot_framework.message.segment;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

/**
 * 视频消息段
 */
@Getter
@Setter
public class VideoMessageSegment extends MessageSegment {
    private final String file;
    private final String url;

    private boolean useCache;
    private boolean useProxy;
    private int timeout;

    public VideoMessageSegment(String file, String url) {
        super("video");
        this.file = file;
        this.url = url;
        this.useCache = true;
        this.useProxy = true;
        this.timeout = -1;
    }

    public VideoMessageSegment(String file) {
        this(file, "");
    }

    public VideoMessageSegment(JSONObject msgData) {
        this(msgData.optString("file", ""), msgData.optString("url", ""));
    }

    @Override
    public String toString() {
        return String.format("[视频:%s]", this.file);
    }

    @Override
    public JSONObject serializeMsgData() {
        JSONObject data = new JSONObject();
        data.put("file", this.file);
        data.put("cache", this.useCache ? "1" : "0");
        data.put("proxy", this.useProxy ? "1" : "0");
        if(this.timeout != -1) {
            data.put("timeout", String.valueOf(this.timeout));
        }
        return data;
    }
}
