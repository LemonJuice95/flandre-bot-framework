package io.lemonjuice.flandre_bot_framework.utils;

import io.lemonjuice.flandre_bot_framework.message.segment.ForwardMessageSegment;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.network.NetworkContainer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageHelper {
    public Message getMessage(long msgId) {
        return MessageParser.parseMessage(this.getMessageRequest(msgId));
    }

    private JSONObject getMessageRequest(long msgId) {
        JSONObject data = new JSONObject();
        data.put("message_id", msgId);
        return NetworkContainer.getImpl().request("get_msg", data).optJSONObject("data", null);
    }

    public List<Message> getForwardMessages(ForwardMessageSegment forwardSeg) {
        return this.getForwardMessages(forwardSeg.getForwardId());
    }

    public List<Message> getForwardMessages(long forwardId) {
        List<Message> result = new ArrayList<>();
        JSONObject response = this.getForwardMsgsRequest(forwardId);
        JSONArray messages = response.optJSONArray("messages", new JSONArray());
        for(int i = 0; i < messages.length(); i++) {
            result.add(MessageParser.parseMessage(messages.getJSONObject(i)));
        }
        return result;
    }

    private JSONObject getForwardMsgsRequest(long forwardId) {
        JSONObject data = new JSONObject();
        data.put("id", forwardId);
        return NetworkContainer.getImpl().request("get_forward_msg", data).optJSONObject("data", null);
    }
}
