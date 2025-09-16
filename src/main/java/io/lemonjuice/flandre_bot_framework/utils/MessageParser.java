package io.lemonjuice.flandre_bot_framework.utils;

import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.model.request.BaseRequest;
import io.lemonjuice.flandre_bot_framework.model.request.FriendRequest;
import io.lemonjuice.flandre_bot_framework.model.request.GroupRequest;
import lombok.extern.log4j.Log4j2;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nullable;

@Log4j2
public class MessageParser {
    @Nullable
    public static Message parseMessage(JSONObject json) {
        try {
            JSONObject senderJson = json.getJSONObject("sender");

            Message.Builder message$builder = new Message.Builder()
                    .selfId(json.getLong("self_id"))
                    .userId(json.getLong("user_id"))
                    .targetId(json.optLong("target_id", -1))
                    .groupId(json.optLong("group_id", -1))
                    .time(json.getInt("time"))
                    .messageId(json.getInt("message_id"))
                    .realId(json.getInt("real_id"))
                    .realSeq(json.getString("real_seq"))
                    .type(json.getString("message_type"))
                    .subType(json.getString("sub_type"))
                    .sender(new Message.Sender(
                            senderJson.getLong("user_id"),
                            senderJson.getString("nickname"),
                            senderJson.getString("card"),
                            senderJson.optString("role", "")
                    ))
                    .rawMessage(json.getString("raw_message"))
                    .message(json.getString("message")
                            .replace("&#91;", "[")
                            .replace("&#93;", "]")
                            .replace("&amp;", "&"))
                    .font(json.getInt("font"))
                    .format(json.getString("message_format"));
            return message$builder.build();
        } catch (JSONException e) {
            log.error("消息解析异常! 原始json文本: \"{}\"", json.toString(), e);
            return null;
        }
    }

    @Nullable
    public static GroupRequest parseGroupRequest(JSONObject json) {
        try {
            GroupRequest.Builder request = ((GroupRequest.Builder) new GroupRequest.Builder()
                    .time(json.getLong("time"))
                    .selfId(json.getLong("self_id"))
                    .userId(json.getLong("user_id"))
                    .comment(json.getString("comment"))
                    .flag(json.getString("flag")))
                    .groupId(json.getLong("group_id"))
                    .type(GroupRequest.Type.valueOf(json.getString("sub_type").toUpperCase()));
            return request.build();
        } catch (JSONException e) {
            log.error("群聊请求解析异常! 原始json文本: \"{}\"", json.toString(), e);
            return null;
        }
    }

    @Nullable
    public static FriendRequest parseFriendRequest(JSONObject json) {
        try {
            BaseRequest.Builder<FriendRequest> request = new FriendRequest.Builder()
                    .time(json.getLong("time"))
                    .selfId(json.getLong("self_id"))
                    .userId(json.getLong("user_id"))
                    .comment(json.getString("comment"))
                    .flag(json.getString("flag"));
            return request.build();
        } catch (JSONException e) {
            log.error("加好友请求解析异常! 原始json文本: \"{}\"", json.toString(), e);
            return null;
        }
    }
}
