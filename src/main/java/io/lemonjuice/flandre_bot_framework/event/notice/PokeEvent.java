package io.lemonjuice.flandre_bot_framework.event.notice;

import io.lemonjuice.flandre_bot_framework.account.ContextManager;
import io.lemonjuice.flandre_bot_framework.message.FriendContext;
import io.lemonjuice.flandre_bot_framework.message.GroupContext;
import io.lemonjuice.flandre_bot_framework.message.IMessageContext;
import lombok.Getter;
import org.json.JSONObject;

@Getter
public class PokeEvent extends NoticeEvent {
    protected final IMessageContext context;
    protected final long targetId;

    public PokeEvent(long time, long selfId, long userId, long targetId) {
        super(time, selfId, NoticeType.NOTIFY, userId);
        this.targetId = targetId;
        this.context = ContextManager.getFriend(userId);
    }

    public PokeEvent(long time, long selfId, long userId, long groupId, long targetId) {
        super(time, selfId, NoticeType.NOTIFY, userId);
        this.targetId = targetId;
        this.context = ContextManager.getGroup(groupId);
    }

    private PokeEvent(JSONObject noticeJson) {
        super(noticeJson);
        this.targetId = noticeJson.optLong("target_id");
        if(noticeJson.has("group_id") && noticeJson.getLong("group_id") > 0) {
            this.context = ContextManager.getGroup(noticeJson.getLong("group_id"));
        } else {
            this.context = ContextManager.getFriend(this.userId);
        }
    }

    public static PokeEvent createFromJson(JSONObject noticeJson) {
        if(noticeJson.has("group_id") && noticeJson.getLong("group_id") > 0) {
            return new Group(noticeJson);
        } else {
            return new Friend(noticeJson);
        }
    }

    @Getter
    public static class Group extends PokeEvent {
        protected final long groupId;
        protected final GroupContext groupContext;

        public Group(long time, long selfId, long userId, long groupId, long targetId) {
            super(time, selfId, userId, groupId, targetId);
            this.groupId = groupId;
            this.groupContext = (GroupContext) this.context;
        }

        Group(JSONObject noticeJson) {
            super(noticeJson);
            this.groupId = noticeJson.optLong("group_id", -1L);
            this.groupContext = (GroupContext) this.context;
        }
    }

    @Getter
    public static class Friend extends PokeEvent {
        protected final FriendContext friendContext;

        public Friend(long time, long selfId, long userId, long targetId) {
            super(time, selfId, userId, targetId);
            this.friendContext = (FriendContext) this.context;
        }

        Friend(JSONObject noticeJson) {
            super(noticeJson);
            this.friendContext = (FriendContext) this.context;
        }
    }
}