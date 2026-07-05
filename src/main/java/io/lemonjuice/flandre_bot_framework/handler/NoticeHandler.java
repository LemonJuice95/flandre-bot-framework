package io.lemonjuice.flandre_bot_framework.handler;

import io.lemonjuice.flandre_bot_framework.event.BotEventBus;
import io.lemonjuice.flandre_bot_framework.event.notice.*;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NoticeHandler {
    private static final Map<String, Function<JSONObject, NoticeEvent>> EVENT_FACTORIES;

    static {
        EVENT_FACTORIES = Map.ofEntries(
                Map.entry("group_upload", GroupUploadFileEvent::new),
                Map.entry("group_admin", GroupAdminChangeEvent::new),
                Map.entry("group_decrease", GroupMemberChangeEvent.Decrease::new),
                Map.entry("group_increase", GroupMemberChangeEvent.Increase::new),
                Map.entry("group_ban", GroupMemberMuteEvent::new),
                Map.entry("friend_add", FriendAddedEvent::new),
                Map.entry("group_recall", GroupMessageRecallEvent::new),
                Map.entry("friend_recall", FriendMessageRecallEvent::new),
                Map.entry("notify.poke", PokeEvent::createFromJson),
                Map.entry("notify.lucky_king", GroupLuckyKingEvent::new),
                Map.entry("notify.honor", GroupMemberHonorEvent::new)
        );
    }

    public static void handle(JSONObject json) {
        String factoryKey = json.optString("notice_type", "");
        if(factoryKey.equals("notify")) {
            factoryKey = factoryKey + "." + json.optString("sub_type", "");
        }

        Function<JSONObject, NoticeEvent> eventFactory = EVENT_FACTORIES.get(factoryKey);
        if(eventFactory != null) {
            BotEventBus.post(eventFactory.apply(json));
        }

        BotEventBus.post(new NoticeJsonEvent(json));
    }
}
