package io.lemonjuice.flandre_bot.event.msg;

import io.lemonjuice.flandre_bot.event.Event;
import io.lemonjuice.flandre_bot.model.Message;
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
