package io.lemonjuice.flandre_bot_framework.message;

import io.lemonjuice.flandre_bot_framework.account.ContextManager;
import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;
import io.lemonjuice.flandre_bot_framework.network.NetworkContainer;
import io.lemonjuice.flandre_bot_framework.network.WSClient;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

@Getter
public class FriendContext extends MessageContext {
    protected final long friendId;
    protected final String nickname;

    public FriendContext(long friendId) {
        this.friendId = friendId;
        this.nickname = ContextManager.getFriend(friendId).getNickname();
    }

    public FriendContext(long friendId, String nickname) {
        this.friendId = friendId;
        this.nickname = nickname;
    }

    @Override
    public void sendMessage(List<MessageSegment> segments) {
        JSONArray msgArray = new JSONArray();
        segments.forEach(seg -> msgArray.put(seg.serialize()));

        JSONObject msg = new JSONObject();
        msg.put("user_id", this.friendId);
        msg.put("message", msgArray);

        NetworkContainer.getImpl().sendMsg("send_private_msg", msg);
    }

    @Override
    public void sendText(String message) {
        this.sendMessage(List.of(new TextMessageSegment(message)));
    }

    @Override
    public void sendForwardMessage(List<List<MessageSegment>> messages) {
        JSONObject msg = new JSONObject();
        msg.put("user_id", this.friendId);
        JSONArray jsonArray = new JSONArray();
        JSONObject node = new JSONObject();
        JSONObject data = new JSONObject();
        for(List<MessageSegment> m : messages) {
            node.put("type", "node");
            if(this.getBotId() != -1)
                data.put("user_id", this.getBotId());
            JSONArray content = new JSONArray();
            m.forEach(seg -> content.put(seg.serialize()));
            data.put("content", content);
            node.put("data", data);
            jsonArray.put(node);
            node = new JSONObject();
            data = new JSONObject();
        }
        msg.put("messages", jsonArray);
        NetworkContainer.getImpl().sendMsg("send_private_forward_msg", msg);
    }
}
