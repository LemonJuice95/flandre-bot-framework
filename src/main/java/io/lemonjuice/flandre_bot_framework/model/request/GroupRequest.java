package io.lemonjuice.flandre_bot_framework.model.request;

import io.lemonjuice.flandre_bot_framework.event.request.GroupRequestEvent;
import io.lemonjuice.flandre_bot_framework.network.WSClientCore;
import org.json.JSONObject;

import java.util.function.Function;

public class GroupRequest extends BaseRequest {
    public final long groupId;
    public final Type type;

    private GroupRequest(long time, long selfId, long userId, String comment, String flag, long groupId, Type type) {
        super(time, selfId, userId, comment, flag);
        this.groupId = groupId;
        this.type = type;
    }

    /**
     * 同意加群/群邀请请求
     */
    public void accept() {
        WSClientCore.getInstance().sendJson(this.constructJson(true, ""));
    }

    /**
     * 拒绝加群/群邀请请求
     */
    public void deny() {
        WSClientCore.getInstance().sendJson(this.constructJson(false, ""));
    }

    /**
     * 拒绝加群/群邀请请求
     * @param reason 拒绝原因
     */
    public void deny(String reason) {
        WSClientCore.getInstance().sendJson(this.constructJson(false, reason));
    }

    private JSONObject constructJson(boolean approve, String reason) {
        JSONObject msg = new JSONObject();
        msg.put("action", "set_group_add_request");
        JSONObject params = new JSONObject();
        params.put("flag", this.flag);
        params.put("approve", approve);
        if(!approve && !reason.isEmpty()) {
            params.put("reason", reason);
        }
        msg.put("params", params);
        return msg;
    }

    public static class Builder extends BaseRequest.Builder<GroupRequest> {
        public long groupId;
        public Type type;

        public Builder groupId(long groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        @Override
        public GroupRequest build() {
            return new GroupRequest(this.time, this.selfId, this.userId, this.comment, this.flag, this.groupId, this.type);
        }
    }

    public enum Type {
        INVITE(GroupRequestEvent.Invite::new),
        ADD(GroupRequestEvent.Add::new);

        private final Function<GroupRequest, GroupRequestEvent> eventProvider;

        private Type(Function<GroupRequest, GroupRequestEvent> eventProvider) {
            this.eventProvider = eventProvider;
        }

        public GroupRequestEvent provideEvent(GroupRequest request) {
            return this.eventProvider.apply(request);
        }
    }
}
