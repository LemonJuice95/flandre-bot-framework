package io.lemonjuice.flandre_bot_framework.message.segment;

import io.lemonjuice.flandre_bot_framework.utils.MessageParser;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 合并转发的消息节点
 */
@Getter
public class ForwardNodeMessageSegment extends MessageSegment {
    private final long userId;
    @Nullable
    private final String nickname;
    private final List<MessageSegment> content;

    public ForwardNodeMessageSegment(long userId, String nickname, List<MessageSegment> content) {
        super("node");
        this.userId = userId;
        this.nickname = nickname;
        this.content = content;
    }

    public ForwardNodeMessageSegment(long userId, String nickname) {
        this(userId, nickname, new ArrayList<>());
    }

    public ForwardNodeMessageSegment() {
        this(-1, null, new ArrayList<>());
    }

    public ForwardNodeMessageSegment(JSONObject msgData) {
        super("node");
        long userId;
        try {
            userId = Long.parseLong(msgData.getString("user_id"));
        } catch (JSONException | NumberFormatException e) {
            userId = -1;
        }
        this.userId = userId;
        this.nickname = msgData.optString("nickname", null);
        this.content = new ArrayList<>();
        JSONArray contentJson = msgData.optJSONArray("content");
        for(int i = 0; i < contentJson.length(); i++) {
            JSONObject segJson = contentJson.getJSONObject(i);
            MessageSegment msgSeg = MessageParser.parseMessageSegment(segJson);
            this.content.add(msgSeg);
        }
    }

    public void addSeg(MessageSegment msgSeg) {
        this.content.add(msgSeg);
    }

    @Override
    public String toString() {
        return "[合并转发消息节点]";
    }

    @Override
    public JSONObject serializeMsgData() {
        JSONObject data = new JSONObject();
        if(this.userId != -1) {
            data.put("user_id", -1);
        }
        if(this.nickname != null) {
            data.put("nickname", this.nickname);
        }
        JSONArray contentJson = new JSONArray();
        for(MessageSegment msgSeg : this.content) {
            contentJson.put(msgSeg.serialize());
        }
        data.put("content", contentJson);
        return data;
    }
}
