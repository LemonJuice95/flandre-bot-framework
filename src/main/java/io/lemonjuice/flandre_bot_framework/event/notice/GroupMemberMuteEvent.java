package io.lemonjuice.flandre_bot_framework.event.notice;

import lombok.Getter;
import org.json.JSONObject;

@Getter
public class GroupMemberMuteEvent extends GroupNoticeEvent {
    protected final long operatorId;
    protected final Type subType;
    protected final long duration;

    public GroupMemberMuteEvent(long time, long selfId, long userId, long groupId,
                                long operatorId, Type subType, long duration) {
        super(time, selfId, NoticeType.GROUP_MEMBER_MUTE, userId, groupId);
        this.operatorId = operatorId;
        this.subType = subType;
        this.duration = duration;
    }

    public GroupMemberMuteEvent(JSONObject noticeJson) {
        super(noticeJson);
        this.operatorId = noticeJson.optLong("operator_id", -1L);
        this.subType = Type.forName(noticeJson.optString("sub_type", ""));
        this.duration = noticeJson.optLong("duration", -1L);
    }

    public enum Type {
        MUTE,
        UNMUTE,
        UNKNOWN;

        public static Type forName(String name) {
            switch (name) {
                case "ban" -> {
                    return MUTE;
                }
                case "lift_ban" -> {
                    return UNMUTE;
                }
                default -> {
                    return UNKNOWN;
                }
            }
        }
    }
}
