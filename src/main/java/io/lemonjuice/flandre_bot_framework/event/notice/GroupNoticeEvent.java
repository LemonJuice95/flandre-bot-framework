package io.lemonjuice.flandre_bot_framework.event.notice;

import io.lemonjuice.flandre_bot_framework.account.ContextManager;
import io.lemonjuice.flandre_bot_framework.message.GroupContext;
import lombok.Getter;
import org.json.JSONObject;

@Getter
public class GroupNoticeEvent extends NoticeEvent {
    protected final GroupContext context;
    protected final long groupId;

    public GroupNoticeEvent(long time, long selfId, NoticeType type, long userId, long groupId) {
        super(time, selfId, type, userId);
        this.groupId = groupId;
        this.context = ContextManager.getGroup(groupId);
    }

    public GroupNoticeEvent(JSONObject noticeJson) {
        super(noticeJson);
        this.groupId = noticeJson.optLong("group_id", -1L);
        this.context = ContextManager.getGroup(this.groupId);
    }
}
