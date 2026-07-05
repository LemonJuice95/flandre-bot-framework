package io.lemonjuice.flandre_bot_framework.event.notice;

import lombok.Getter;
import org.json.JSONObject;

@Getter
public class GroupMessageRecallEvent extends GroupNoticeEvent {
    protected final long operatorId;
    protected final long messageId;

    public GroupMessageRecallEvent(long time, long selfId, long userId, long groupId,
                                   long operatorId, long messageId) {
        super(time, selfId, NoticeType.GROUP_MESSAGE_RECALL, userId, groupId);
        this.operatorId = operatorId;
        this.messageId = messageId;
    }

    public GroupMessageRecallEvent(JSONObject noticeJson) {
        super(noticeJson);
        this.operatorId = noticeJson.optLong("operator_id", -1L);
        this.messageId = noticeJson.optLong("message_id", -1L);
    }
}
