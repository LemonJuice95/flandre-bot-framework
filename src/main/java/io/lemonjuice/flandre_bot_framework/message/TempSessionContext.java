package io.lemonjuice.flandre_bot_framework.message;

import io.lemonjuice.flandre_bot_framework.network.NetworkContainer;
import io.lemonjuice.flandre_bot_framework.network.WSClient;
import lombok.Getter;
import org.json.JSONObject;

@Getter
public class TempSessionContext extends MessageContext {
    protected final long userId;
    protected final long groupId;

    public TempSessionContext(long userId, long groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    @Override
    public void sendText(String message, boolean sendAsRawText) {
        JSONObject msg = new JSONObject();
        msg.put("group_id", this.groupId);
        msg.put("user_id", this.userId);
        msg.put("message", message);
        msg.put("auto_escape", sendAsRawText);
        NetworkContainer.getImpl().sendMsg("send_private_msg", msg);
    }
}
