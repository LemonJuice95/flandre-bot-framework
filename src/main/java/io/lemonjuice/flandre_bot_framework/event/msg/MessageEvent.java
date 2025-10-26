package io.lemonjuice.flandre_bot_framework.event.msg;

import io.lemonjuice.flandre_bot_framework.event.Event;
import io.lemonjuice.flandre_bot_framework.model.Message;
import lombok.Getter;

@Getter
public abstract class MessageEvent extends Event {
    private final Message message;

    private MessageEvent(Message message) {
        this.message = message;
    }

    /**
     * 推送时机: 接收到群聊消息时
     */
    public static class Group extends MessageEvent {
        public Group(Message message) {
            super(message);
        }
    }

    public static abstract class Private extends MessageEvent {
        public Private(Message message) {
            super(message);
        }
    }

    /**
     * 推送时机: 接收到好友消息时
     */
    public static class Friend extends Private {
        public Friend(Message message) {
            super(message);
        }
    }

    /**
     * 推送时机: 接收到临时会话消息时
     */
    public static class TempSession extends Private {
        public TempSession(Message message) {
            super(message);
        }
    }
}
