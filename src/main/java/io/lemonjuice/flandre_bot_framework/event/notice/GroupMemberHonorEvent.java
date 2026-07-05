package io.lemonjuice.flandre_bot_framework.event.notice;

import lombok.Getter;
import org.json.JSONObject;

@Getter
public class GroupMemberHonorEvent extends GroupNoticeEvent {
    protected final Type honorType;

    public GroupMemberHonorEvent(long time, long selfId, long userId, long groupId, Type honorType) {
        super(time, selfId, NoticeType.NOTIFY, userId, groupId);
        this.honorType = honorType;
    }

    public GroupMemberHonorEvent(JSONObject noticeJson) {
        super(noticeJson);
        this.honorType = Type.forName(noticeJson.optString("honor_type", ""));
    }

    public enum Type {
        TALKATIVE, //龙王
        PERFORMER, //群聊之火
        EMOTION, //快乐源泉
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
