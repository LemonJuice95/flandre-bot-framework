package io.lemonjuice.flandre_bot_framework.event.notice;

import io.lemonjuice.flandre_bot_framework.account.ContextManager;
import io.lemonjuice.flandre_bot_framework.message.FriendContext;
import lombok.Getter;
import org.json.JSONObject;

@Getter
public class FriendMessageRecallEvent extends NoticeEvent {
    protected final FriendContext context;
    protected final long messageId;

    public FriendMessageRecallEvent(long time, long selfId, long userId, long messageId) {
        super(time, selfId, NoticeType.FRIEND_MESSAGE_RECALL, userId);
        this.messageId = messageId;
        this.context = ContextManager.getFriend(userId);
    }

    public FriendMessageRecallEvent(JSONObject noticeJson) {
        super(noticeJson);
        this.messageId = noticeJson.optLong("message_id", -1L);
        this.context = ContextManager.getFriend(this.userId);
    }
}
