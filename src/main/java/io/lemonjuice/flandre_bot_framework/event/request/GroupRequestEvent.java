package io.lemonjuice.flandre_bot_framework.event.request;

import io.lemonjuice.flandre_bot_framework.event.Event;
import io.lemonjuice.flandre_bot_framework.model.request.GroupRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class GroupRequestEvent extends Event {
    private final GroupRequest request;

    /**
     * 推送时机: 收到群邀请请求时
     */
    public static class Invite extends GroupRequestEvent {
        public Invite(GroupRequest request) {
            super(request);
        }
    }

    /**
     * 推送时机: 收到加群请求时
     */
    public static class Add extends GroupRequestEvent {
        public Add(GroupRequest request) {
            super(request);
        }
    }
}
