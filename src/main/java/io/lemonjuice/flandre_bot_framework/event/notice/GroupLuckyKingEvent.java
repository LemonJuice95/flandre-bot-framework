package io.lemonjuice.flandre_bot_framework.event.notice;

import lombok.Getter;
import org.json.JSONObject;

@Getter
public class GroupLuckyKingEvent extends GroupNoticeEvent {
    protected final long targetId;

    public GroupLuckyKingEvent(long time, long selfId, long userId, long groupId, long targetId) {
        super(time, selfId, NoticeType.NOTIFY, userId, groupId);
        this.targetId = targetId;
    }

    public GroupLuckyKingEvent(JSONObject noticeJson) {
        super(noticeJson);
        this.targetId = noticeJson.optLong("target_id", -1L);
    }
}
