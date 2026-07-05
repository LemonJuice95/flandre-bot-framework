package io.lemonjuice.flandre_bot_framework.event.notice;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum NoticeType {
    GROUP_FILE_UPLOAD("group_upload"),
    GROUP_ADMIN_CHANGE("group_admin"),
    GROUP_MEMBER_DECREASE("group_decrease"),
    GROUP_MEMBER_INCREASE("group_increase"),
    GROUP_MEMBER_MUTE("group_ban"),
    FRIEND_ADD("friend_add"),
    GROUP_MESSAGE_RECALL("group_recall"),
    FRIEND_MESSAGE_RECALL("friend_recall"),
    NOTIFY("notify"),
    UNKNOWN("");

    private static final Map<String, NoticeType> TYPES;
    protected final String name;

    private NoticeType(String name) {
        this.name = name;
    }

    static {
        Map<String, NoticeType> typeMap = new HashMap<>();
        Arrays.stream(NoticeType.values()).forEach(type -> {
            typeMap.put(type.name, type);
        });
        TYPES = Collections.unmodifiableMap(typeMap);
    }

    public static NoticeType forName(String name) {
        return TYPES.getOrDefault(name, UNKNOWN);
    }
}
