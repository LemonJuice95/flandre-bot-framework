package io.lemonjuice.flandre_bot_framework.utils;

import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.network.NetworkContainer;
import org.json.JSONObject;

public class MessageHelper {
    public Message getMessage(long msgId) {
        return MessageParser.parseMessage(this.getMessageRequest(msgId));
    }

    private JSONObject getMessageRequest(long msgId) {
        JSONObject data = new JSONObject();
        data.put("message_id", msgId);
        return NetworkContainer.getImpl().request("get_msg", data).getJSONObject("data");
    }
}
