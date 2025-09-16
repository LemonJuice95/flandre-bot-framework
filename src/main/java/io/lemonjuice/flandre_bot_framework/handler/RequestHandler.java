package io.lemonjuice.flandre_bot_framework.handler;

import io.lemonjuice.flandre_bot_framework.event.BotEventBus;
import io.lemonjuice.flandre_bot_framework.event.request.FriendRequestEvent;
import io.lemonjuice.flandre_bot_framework.model.request.FriendRequest;
import io.lemonjuice.flandre_bot_framework.model.request.GroupRequest;
import io.lemonjuice.flandre_bot_framework.utils.MessageParser;
import org.json.JSONObject;

public class RequestHandler {
    public static void handle(JSONObject requestJson) {
        String requestType = requestJson.optString("request_type", "");
        if(requestType.equals("group")) {
            GroupRequest groupRequest = MessageParser.parseGroupRequest(requestJson);
            if(groupRequest != null) {
                BotEventBus.post(groupRequest.type.provideEvent(groupRequest));
            }
        }

        if(requestType.equals("friend")) {
            FriendRequest friendRequest = MessageParser.parseFriendRequest(requestJson);
            if(friendRequest != null) {
                BotEventBus.post(new FriendRequestEvent(friendRequest));
            }
        }
    }
}
