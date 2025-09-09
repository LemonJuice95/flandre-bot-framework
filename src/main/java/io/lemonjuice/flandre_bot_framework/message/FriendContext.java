package io.lemonjuice.flandre_bot_framework.message;

import io.lemonjuice.flandre_bot_framework.network.WSClientCore;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

@Getter
public class FriendContext extends MessageContext {
    private final long friendId;

    public FriendContext(long friendId) {
        this.friendId = friendId;
    }

    @Override
    public void sendText(String message, boolean sendAsRawText) {
        JSONObject json = new JSONObject();
        json.put("action", "send_private_msg");
        JSONObject params = new JSONObject();
        params.put("user_id", this.friendId);
        params.put("message", message);
        params.put("auto_escape", sendAsRawText);
        json.put("params", params);
        WSClientCore.getInstance().sendText(json.toString());
    }

    @Override
    public void sendForwardText(List<String> messages) {
        JSONObject json = new JSONObject();
        json.put("action", "send_private_forward_msg");
        JSONObject params = new JSONObject();
        params.put("user_id", this.friendId);
        JSONArray jsonArray = new JSONArray();
        JSONObject node = new JSONObject();
        JSONObject data = new JSONObject();
        for(String m : messages) {
            node.put("type", "node");
            if(this.getBotId() != -1)
                data.put("user_id", this.getBotId());
            data.put("content", m);
            node.put("data", data);
            jsonArray.put(node);
            node = new JSONObject();
            data = new JSONObject();
        }
        params.put("messages", jsonArray);
        json.put("params", params);
        WSClientCore.getInstance().sendText(json.toString());
    }
}
