package io.lemonjuice.flandre_bot_framework.message;

import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;
import io.lemonjuice.flandre_bot_framework.network.NetworkContainer;
import io.lemonjuice.flandre_bot_framework.network.WSClient;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

@Getter
public class TempSessionContext extends MessageContext {
    protected final long userId;
    protected final long groupId;

    public TempSessionContext(long userId, long groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    @Override
    public void sendMessage(List<MessageSegment> segments) {
        JSONArray msgArray = new JSONArray();
        segments.forEach(seg -> msgArray.put(seg.serialize()));

        JSONObject msg = new JSONObject();
        msg.put("group_id", this.groupId);
        msg.put("user_id", this.userId);
        msg.put("message", msgArray);

        NetworkContainer.getImpl().sendMsg("send_private_msg", msg);
    }

    @Override
    public void sendText(String message) {
        this.sendMessage(List.of(new TextMessageSegment(message)));
    }
}
