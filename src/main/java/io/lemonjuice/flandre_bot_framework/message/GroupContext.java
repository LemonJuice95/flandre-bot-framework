package io.lemonjuice.flandre_bot_framework.message;

import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;
import io.lemonjuice.flandre_bot_framework.model.GroupMember;
import io.lemonjuice.flandre_bot_framework.network.NetworkContainer;
import io.lemonjuice.flandre_bot_framework.network.WSClient;
import io.lemonjuice.flandre_bot_framework.account.ContextManager;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Getter
public class GroupContext extends MessageContext {
    protected final long groupId;
    protected final String groupName;
    protected final List<GroupMember> members;

    public GroupContext(long groupId) {
        this.groupId = groupId;
        this.groupName = ContextManager.getGroup(groupId).getGroupName();
        this.members = ContextManager.getGroup(groupId).getMembers();
    }

    public GroupContext(long groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.members = new ArrayList<>();
    }

    public void initMembers() {
        JSONObject request = new JSONObject();
        request.put("group_id", this.groupId);
        JSONObject result = NetworkContainer.getImpl().request("get_group_member_list", request);
        JSONArray members = result.getJSONArray("data");
        for(int i = 0; i < members.length(); i++) {
            JSONObject memberI = members.getJSONObject(i);
            try {
                this.members.add(new GroupMember(
                        memberI.getLong("user_id"),
                        memberI.getString("nickname"),
                        memberI.getString("card"),
                        memberI.getString("role")
                ));
            } catch (JSONException e) {
                log.warn("初始化群聊{}的成员列表时发生问题", this.groupId, e);
            }
        }
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
