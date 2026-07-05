package io.lemonjuice.flandre_bot_framework.event.notice;

import lombok.Getter;
import org.json.JSONObject;

@Getter
public class GroupMemberChangeEvent extends GroupNoticeEvent {
    protected final long operatorId;
    protected final Type subType;

    public GroupMemberChangeEvent(long time, long selfId, NoticeType type, long userId, long groupId,
                                  long operatorId, Type subType) {
        super(time, selfId, type, userId, groupId);
        this.operatorId = operatorId;
        this.subType = subType;
    }

    public GroupMemberChangeEvent(JSONObject noticeJson) {
        super(noticeJson);
        this.operatorId = noticeJson.optLong("operator_id", -1L);
        this.subType = Type.forName(noticeJson.optString("sub_type", ""));
    }

    public static class Increase extends GroupMemberChangeEvent {
        public Increase(long time, long selfId, long userId, long groupId, long operatorId, Type subType) {
            super(time, selfId, NoticeType.GROUP_MEMBER_INCREASE, userId, groupId, operatorId, subType);
        }

        public Increase(JSONObject noticeJson) {
            super(noticeJson);
        }
    }

    public static class Decrease extends GroupMemberChangeEvent {
        public Decrease(long time, long selfId, long userId, long groupId, long operatorId, Type subType) {
            super(time, selfId, NoticeType.GROUP_MEMBER_DECREASE, userId, groupId, operatorId, subType);
        }

        public Decrease(JSONObject noticeJson) {
            super(noticeJson);
        }
    }

    public enum Type {
        //群成员减少
        LEAVE,
        KICK,
        KICK_ME,

        //群成员增加
        APPROVE,
        INVITE,

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
