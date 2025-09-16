package io.lemonjuice.flandre_bot_framework.handler;

import io.lemonjuice.flandre_bot_framework.event.BotEventBus;
import io.lemonjuice.flandre_bot_framework.event.notice.NoticeJsonEvent;
import org.json.JSONObject;

public class NoticeHandler {
    //TODO OneBot通知有点杂，暂时想不出来怎么处理
    public static void handle(JSONObject json) {
        BotEventBus.post(new NoticeJsonEvent(json));
    }
}
