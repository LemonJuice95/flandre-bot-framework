package io.lemonjuice.flandre_bot_framework.message.segment;

import lombok.Getter;
import org.json.JSONObject;

/**
 * 链接分享消息段
 */
@Getter
public class ShareMessageSegment extends MessageSegment {
    private final String url;
    private final String title;
    private final String content;
    private final String imageUrl;

    public ShareMessageSegment(String url, String title, String content, String imageUrl) {
        super("share");
        this.url = url;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public ShareMessageSegment(JSONObject msgData) {
        this(
           msgData.optString("url", ""),
           msgData.optString("title", ""),
           msgData.optString("content", ""),
           msgData.optString("image", "")
        );
    }

    @Override
    public String toString() {
        return String.format("[分享链接[%s]:%s]", this.title, this.url);
    }

    @Override
    public JSONObject serializeMsgData() {
        JSONObject data = new JSONObject();
        data.put("url", this.url);
        data.put("title", this.title);
        if(!this.content.isEmpty()) {
            data.put("content", this.content);
        }
        if(!this.imageUrl.isEmpty()) {
            data.put("image", this.imageUrl);
        }
        return data;
    }
}
