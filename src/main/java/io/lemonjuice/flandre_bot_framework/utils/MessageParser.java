package io.lemonjuice.flandre_bot_framework.utils;

import io.lemonjuice.flandre_bot_framework.event.BotEventBus;
import io.lemonjuice.flandre_bot_framework.event.meta.SegmentTypeRegisterEvent;
import io.lemonjuice.flandre_bot_framework.message.MessageSegmentList;
import io.lemonjuice.flandre_bot_framework.message.segment.*;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.model.request.BaseRequest;
import io.lemonjuice.flandre_bot_framework.model.request.FriendRequest;
import io.lemonjuice.flandre_bot_framework.model.request.GroupRequest;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Log4j2
public class MessageParser {
    public static final Map<String, Function<JSONObject, MessageSegment>> MESSAGE_SEGMENT_TYPES = new HashMap<>();

    public static void initSegmentMap() {
        MESSAGE_SEGMENT_TYPES.put("at", AtMessageSegment::new);
        MESSAGE_SEGMENT_TYPES.put("contact", ContactMessageSegment::new);
        MESSAGE_SEGMENT_TYPES.put("dice", DiceMessageSegment::new);
        MESSAGE_SEGMENT_TYPES.put("forward", ForwardMessageSegment::new);
        MESSAGE_SEGMENT_TYPES.put("node", ForwardNodeMessageSegment::new);
        MESSAGE_SEGMENT_TYPES.put("image", ImageMessageSegment::new);
        MESSAGE_SEGMENT_TYPES.put("json", JsonMessageSegment::deserializeMsgData);
        MESSAGE_SEGMENT_TYPES.put("location", LocationMessageSegment::new);
        MESSAGE_SEGMENT_TYPES.put("music", MusicMessageSegment::new);
        MESSAGE_SEGMENT_TYPES.put("poke", PokeMessageSegment::new);
        MESSAGE_SEGMENT_TYPES.put("face", QQFaceMessageSegment::new);
        MESSAGE_SEGMENT_TYPES.put("record", RecordMessageSegment::new);
        MESSAGE_SEGMENT_TYPES.put("reply", ReplyMessageSegment::new);
        MESSAGE_SEGMENT_TYPES.put("rps", RPSMessageSegment::new);
        MESSAGE_SEGMENT_TYPES.put("share", ShareMessageSegment::new);
        MESSAGE_SEGMENT_TYPES.put("text", TextMessageSegment::new);
        MESSAGE_SEGMENT_TYPES.put("video", VideoMessageSegment::new);
        MESSAGE_SEGMENT_TYPES.put("xml", XmlMessageSegment::new);
        MESSAGE_SEGMENT_TYPES.put("markdown", MarkdownMessageSegment::new);
        BotEventBus.post(new SegmentTypeRegisterEvent(MESSAGE_SEGMENT_TYPES));
    }

    @Nullable
    public static MessageSegment parseMessageSegment(JSONObject json) {
        try {
            String type = json.getString("type");
            JSONObject msgData = json.getJSONObject("data");
            return MESSAGE_SEGMENT_TYPES.get(type).apply(msgData);
        } catch (JSONException | NullPointerException e) {
            log.error("解析消息段失败！", e);
            return null;
        }
    }

    public static MessageSegmentList parseArrayMessage(JSONArray array) {
        List<MessageSegment> segments = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            MessageSegment seg = parseMessageSegment(array.getJSONObject(i));
            if(seg instanceof TextMessageSegment newTextSeg &&
               !segments.isEmpty() &&
                segments.getLast() instanceof TextMessageSegment textSeg) {
                MessageSegment newSeg = new TextMessageSegment(textSeg.getContent() + newTextSeg.getContent());
                segments.removeLast();
                segments.add(newSeg);
            } else {
                segments.add(parseMessageSegment(array.getJSONObject(i)));
            }
        }
        return new MessageSegmentList(segments);
    }

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
                    .message(parseArrayMessage(json.getJSONArray("message")))
                    .font(json.getInt("font"))
                    .format(json.getString("message_format"));
            return message$builder.build();
        } catch (Exception e) {
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
