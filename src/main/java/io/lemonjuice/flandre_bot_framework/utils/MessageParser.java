package io.lemonjuice.flandre_bot_framework.utils;

import io.lemonjuice.flandre_bot_framework.model.Message;
import lombok.extern.log4j.Log4j2;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nullable;

@Log4j2
public class MessageParser {
    @Nullable
    public static Message tryParse(JSONObject json) {
        try {
            Message message = new Message();

            message.selfId = json.getLong("self_id");
            message.userId = json.getLong("user_id");
            message.time = json.getInt("time");
            message.messageId = json.getInt("message_id");
            message.realId = json.getInt("real_id");
            message.realSeq = json.getString("real_seq");

            message.type = json.getString("message_type");
            message.subType = json.getString("sub_type");

            Message.Sender sender = new Message.Sender();
            JSONObject senderJson = json.getJSONObject("sender");
            sender.userId = senderJson.getLong("user_id");
            sender.nickName = senderJson.getString("nickname");
            sender.card = senderJson.getString("card");
            sender.role = senderJson.optString("role", "");
            message.sender = sender;

            message.rawMessage = json.getString("raw_message");
            message.message = json.getString("message")
                    .replace("&#91;", "[")
                    .replace("&#93;", "]")
                    .replace("&amp;", "&");
            message.font = json.getInt("font");
            message.format = json.getString("message_format");

            message.targetId = json.optLong("target_id", -1);
            message.groupId = json.optLong("group_id", -1);

            message.generateContext();

            return message;
        } catch (JSONException e) {
            log.error("消息解析异常! 原始json文本: \"{}\"", json.toString(), e);
            return null;
        }
    }
}
