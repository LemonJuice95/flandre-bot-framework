package io.lemonjuice.flandre_bot_framework.message.segment;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

/**
 * 语音消息段
 */
@Getter
@Setter
public class RecordMessageSegment extends MessageSegment {
    private final String file;
    private final boolean magic;
    private final String url;

    private boolean useCache;
    private boolean useProxy;
    private int timeout;

    public RecordMessageSegment(String file, boolean magic, String url) {
        super("record");
        this.file = file;
        this.magic = magic;
        this.url = url;
        this.useCache = true;
        this.useProxy = true;
        this.timeout = -1;
    }

    public RecordMessageSegment(String file, boolean magic) {
        this(file, magic, "");
    }

    public RecordMessageSegment(JSONObject msgData) {
        this(msgData.optString("file", ""), msgData.optInt("magic", 0) == 1, msgData.optString("url", ""));
    }

    @Override
    public String toString() {
        return String.format("[%s语音:%s]", this.magic ? "变声" : "", this.file);
    }

    @Override
    public JSONObject serializeMsgData() {
        JSONObject data = new JSONObject();
        data.put("file", this.file);
        if(this.magic) {
            data.put("magic", "1");
        }
        data.put("cache", this.useCache ? "1" : "0");
        data.put("proxy", this.useProxy ? "1" : "0");
        if(this.timeout != -1) {
            data.put("timeout", String.valueOf(this.timeout));
        }
        return data;
    }
}
