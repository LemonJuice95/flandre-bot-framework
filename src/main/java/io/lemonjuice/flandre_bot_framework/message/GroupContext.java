package io.lemonjuice.flandre_bot_framework.message;

import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;
import io.lemonjuice.flandre_bot_framework.network.NetworkContainer;
import io.lemonjuice.flandre_bot_framework.network.WSClient;
import io.lemonjuice.flandre_bot_framework.account.ContextManager;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

@Getter
public class GroupContext extends MessageContext {
    protected final long groupId;
    protected final String groupName;

    public GroupContext(long groupId) {
        this.groupId = groupId;
        this.groupName = ContextManager.getGroup(groupId).getGroupName();
    }

    public GroupContext(long groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
    }

    @Override
    public void sendMessage(List<MessageSegment> segments) {
        JSONArray msgArray = new JSONArray();
        segments.forEach(seg -> msgArray.put(seg.serialize()));

        JSONObject msg = new JSONObject();
        msg.put("group_id", this.groupId);
        msg.put("message", msgArray);

        NetworkContainer.getImpl().sendMsg("send_group_msg", msg);
    }

    @Override
    public void sendText(String message) {
        this.sendMessage(List.of(new TextMessageSegment(message)));
    }

    @Override
    public void sendForwardMessage(List<List<MessageSegment>> messages) {
        JSONObject msg = new JSONObject();
        msg.put("group_id", this.groupId);
        JSONArray jsonArray = new JSONArray();
        JSONObject node = new JSONObject();
        JSONObject data = new JSONObject();
        for(List<MessageSegment> m : messages) {
            node.put("type", "node");
            if(this.getBotId() != -1)
                data.put("user_id", this.getBotId());
            JSONArray msgArray = new JSONArray();
            m.forEach(seg -> msgArray.put(seg.serialize()));
            data.put("content", msgArray);
            node.put("data", data);
            jsonArray.put(node);
            node = new JSONObject();
            data = new JSONObject();
        }
        msg.put("messages", jsonArray);
        NetworkContainer.getImpl().sendMsg("send_group_forward_msg", msg);
    }
}
