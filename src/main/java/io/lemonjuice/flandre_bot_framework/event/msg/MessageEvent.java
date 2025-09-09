package io.lemonjuice.flandre_bot_framework.event.msg;

import io.lemonjuice.flandre_bot_framework.event.Event;
import io.lemonjuice.flandre_bot_framework.model.Message;
import lombok.Getter;

public class MessageEvent extends Event {
    @Getter
    private final Message message;

    private MessageEvent(Message message) {
        this.message = message;
    }

    public static class Group extends MessageEvent {
        public Group(Message message) {
            super(message);
        }
    }

    public static class Private extends MessageEvent {
        public Private(Message message) {
            super(message);
        }
    }
}
