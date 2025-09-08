package io.lemonjuice.flandre_bot.utils;

import io.lemonjuice.flandre_bot.model.Message;
import io.lemonjuice.flandre_bot.network.WSClientCore;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * @deprecated 已经使用 {@link io.lemonjuice.flandre_bot.message.IMessageContext} 包装发送逻辑
 */
@Deprecated(since = "0.2.0", forRemoval = true)
public class SendingUtils {
    /**
     * 发送群聊消息
     * @param groupId 群号
     * @param message 消息内容
     */
    public static void sendGroupText(long groupId, String message) {
        sendGroupText(groupId, message, false);
    }

    /**
     * 发送群聊消息
     * @param groupId 群号
     * @param message 消息内容
     * @param sendAsRawText 是否不对消息内容进行转义
     */
    public static void sendGroupText(long groupId, String message, boolean sendAsRawText) {
        JSONObject json = new JSONObject();
        json.put("action", "send_group_msg");
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("message", message);
        params.put("auto_escape", sendAsRawText);
        json.put("params", params);
        WSClientCore.getInstance().sendText(json.toString());
    }

    /**
     * 对私聊消息进行回复
     * @param source 原消息
     * @param message 需要发送的消息文本
     */
    public static void replyPrivateText(Message source, String message) {
        replyPrivateText(source, message, false);
    }

    /**
     * 对私聊消息进行回复
     * @param source 原消息
     * @param message 需要发送的消息文本
     * @param sendAsRawText 是否不对消息内容进行转义
     */
    public static void replyPrivateText(Message source, String message, boolean sendAsRawText) {
        if(source.type.equals("private") && source.subType.equals("friend")) {
            sendPrivateText(source.userId, message, sendAsRawText);
        }
        if(source.type.equals("private") && source.subType.equals("group")) {
            sendTmpText(source.userId, source.groupId, message, sendAsRawText);
        }
    }

    /**
     * 发送临时会话消息
     * @param userId 发送对象的qq号
     * @param groupId 临时会话来自的群聊的群号
     * @param message 消息内容
     */
    public static void sendTmpText(long userId, long groupId, String message) {
        sendTmpText(userId, groupId, message, false);
    }


    /**
     * 发送临时会话消息
     * @param userId 发送对象的qq号
     * @param groupId 临时会话来自的群聊的群号
     * @param message 消息内容
     * @param sendAsRawText 是否不对消息内容进行转义
     */
    public static void sendTmpText(long userId, long groupId, String message, boolean sendAsRawText) {
        JSONObject json = new JSONObject();
        json.put("action", "send_private_msg");
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("message", message);
        params.put("auto_escape", sendAsRawText);
        json.put("params", params);
        WSClientCore.getInstance().sendText(json.toString());
    }

    /**
     * 发送私聊消息
     * @param userId 发送对象的qq号
     * @param message 消息内容
     */
    public static void sendPrivateText(long userId, String message) {
        sendPrivateText(userId, message, false);
    }

    /**
     * 发送私聊消息
     * @param userId 发送对象的qq号
     * @param message 消息内容
     * @param sendAsRawText 是否不对消息内容进行转义
     */
    public static void sendPrivateText(long userId, String message, boolean sendAsRawText) {
        JSONObject json = new JSONObject();
        json.put("action", "send_private_msg");
        JSONObject params = new JSONObject();
        params.put("user_id", userId);
        params.put("message", message);
        params.put("auto_escape", sendAsRawText);
        json.put("params", params);
        WSClientCore.getInstance().sendText(json.toString());
    }

    /**
     * 发送合并消息到群聊
     * @param selfId bot自身的qq号
     * @param groupId 发送至群聊的群号
     * @param messages 消息内容（列表内一项对应一条消息）
     */
    public static void sendGroupForwardText(long selfId, long groupId, List<String> messages) {
        JSONObject json = new JSONObject();
        json.put("action", "send_group_forward_msg");
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        JSONArray jsonArray = new JSONArray();
        JSONObject node = new JSONObject();
        JSONObject data = new JSONObject();
        for(String m : messages) {
            node.put("type", "node");
            data.put("user_id", selfId);
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
