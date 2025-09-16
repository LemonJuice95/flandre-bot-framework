package io.lemonjuice.flandre_bot_framework.model.request;

import io.lemonjuice.flandre_bot_framework.network.WSClientCore;
import org.json.JSONObject;

public class FriendRequest extends BaseRequest {
    private FriendRequest(long time, long selfId, long userId, String comment, String flag) {
        super(time, selfId, userId, comment, flag);
    }

    /**
     * 同意加好友请求
     */
    public void accept() {
        WSClientCore.getInstance().sendJson(this.constructJson(true, ""));
    }

    /**
     * 同意加好友请求
     * @param remark 好友备注
     */
    public void accept(String remark) {
        WSClientCore.getInstance().sendJson(this.constructJson(true, remark));
    }

    /**
     * 拒绝加好友请求
     */
    public void deny() {
        WSClientCore.getInstance().sendJson(this.constructJson(false, ""));
    }

    private JSONObject constructJson(boolean approve, String remark) {
        JSONObject msg = new JSONObject();
        msg.put("action", "set_friend_add_request");
        JSONObject params = new JSONObject();
        params.put("flag", this.flag);
        params.put("approve", approve);
        if(approve && !remark.isEmpty()) {
            params.put("remark", remark);
        }
        msg.put("params", params);
        return msg;
    }

    public static class Builder extends BaseRequest.Builder<FriendRequest> {
        @Override
        public FriendRequest build() {
            return new FriendRequest(this.time, this.selfId, this.userId, this.comment, this.flag);
        }
    }
}
