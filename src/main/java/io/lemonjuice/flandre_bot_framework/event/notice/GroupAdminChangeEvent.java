package io.lemonjuice.flandre_bot_framework.event.notice;

import lombok.Getter;
import org.json.JSONObject;

@Getter
public class GroupAdminChangeEvent extends GroupNoticeEvent {
    protected final Type subType;

    public GroupAdminChangeEvent(long time, long selfId, long userId, long groupId, Type subType) {
        super(time, selfId, NoticeType.GROUP_ADMIN_CHANGE, userId, groupId);
        this.subType = subType;
    }

    public GroupAdminChangeEvent(JSONObject noticeJson) {
        super(noticeJson);
        this.subType = Type.forName(noticeJson.optString("sub_type", ""));
    }

    public enum Type {
        SET,
        UNSET,
        UNKNOWN;

        public static Type forName(String name) {
            try {
                return Type.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException | NullPointerException e) {
                return UNKNOWN;
            }
        }
    }
}
