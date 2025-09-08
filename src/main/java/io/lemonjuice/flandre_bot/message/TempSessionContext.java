package io.lemonjuice.flandre_bot.message;

import io.lemonjuice.flandre_bot.network.WSClientCore;
import lombok.Getter;
import org.json.JSONObject;

@Getter
public class TempSessionContext extends MessageContext {
    private final long userId;
    private final long groupId;

    public TempSessionContext(long userId, long groupId, long botId) {
        super(botId);
        this.userId = userId;
        this.groupId = groupId;
    }

    @Override
    public void sendText(String message, boolean sendAsRawText) {
        JSONObject json = new JSONObject();
        json.put("action", "send_private_msg");
        JSONObject params = new JSONObject();
        params.put("group_id", this.groupId);
        params.put("user_id", this.userId);
        params.put("message", message);
        params.put("auto_escape", sendAsRawText);
        json.put("params", params);
        WSClientCore.getInstance().sendText(json.toString());
    }
}
