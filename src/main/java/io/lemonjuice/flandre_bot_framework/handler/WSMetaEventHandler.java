package io.lemonjuice.flandre_bot_framework.handler;

import io.lemonjuice.flandre_bot_framework.event.BotEventBus;
import io.lemonjuice.flandre_bot_framework.event.meta.HeartBeatEvent;
import org.json.JSONObject;

public class WSMetaEventHandler {
    public static void handle(JSONObject json) {
        if(json.getString("meta_event_type").equals("heartbeat")) {
            BotEventBus.post(new HeartBeatEvent());
        }
    }
}
