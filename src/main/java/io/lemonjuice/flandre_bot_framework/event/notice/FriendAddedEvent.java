package io.lemonjuice.flandre_bot_framework.event.notice;

import io.lemonjuice.flandre_bot_framework.account.ContextManager;
import io.lemonjuice.flandre_bot_framework.message.FriendContext;
import lombok.Getter;
import org.json.JSONObject;

@Getter
public class FriendAddedEvent extends NoticeEvent {
    protected final FriendContext context;
    
    public FriendAddedEvent(long time, long selfId, long userId) {
        super(time, selfId, NoticeType.FRIEND_ADD, userId);
        this.context = ContextManager.getFriend(userId);
    }
    
    public FriendAddedEvent(JSONObject noticeJson) {
        super(noticeJson);
        this.context = ContextManager.getFriend(this.userId);
    }
}
