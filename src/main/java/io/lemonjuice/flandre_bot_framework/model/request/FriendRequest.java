package io.lemonjuice.flandre_bot_framework.model.request;

import io.lemonjuice.flandre_bot_framework.network.NetworkContainer;
import org.json.JSONObject;

public class FriendRequest extends BaseRequest {
    private FriendRequest(long time, long selfId, long userId, String comment, String flag) {
        super(time, selfId, userId, comment, flag);
    }

    /**
     * 同意加好友请求
     */
    public void accept() {
        this.handle(true, "");
    }

    /**
     * 同意加好友请求
     * @param remark 好友备注
     */
    public void accept(String remark) {
        this.handle(true, remark);
    }

    /**
     * 拒绝加好友请求
     */
    public void deny() {
        this.handle(false, "");
    }

    private void handle(boolean approve, String remark) {
        JSONObject msg = new JSONObject();
        msg.put("flag", this.flag);
        msg.put("approve", approve);
        if(approve && !remark.isEmpty()) {
            msg.put("remark", remark);
        }
        NetworkContainer.getImpl().sendMsg("set_friend_add_request", msg);
    }

    public static class Builder extends BaseRequest.Builder<FriendRequest> {
        @Override
        public FriendRequest build() {
            return new FriendRequest(this.time, this.selfId, this.userId, this.comment, this.flag);
        }
    }
}
