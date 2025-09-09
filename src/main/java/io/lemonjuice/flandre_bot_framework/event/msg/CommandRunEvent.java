package io.lemonjuice.flandre_bot_framework.event.msg;

import io.lemonjuice.flandre_bot_framework.command.CommandRunner;
import io.lemonjuice.flandre_bot_framework.event.Event;
import io.lemonjuice.flandre_bot_framework.event.ICancelableEvent;
import io.lemonjuice.flandre_bot_framework.model.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class CommandRunEvent extends Event {
    private final Message message;
    private final CommandRunner commandRunner;

    public static class Pre extends CommandRunEvent implements ICancelableEvent {
        public Pre(Message message, CommandRunner runner) {
            super(message, runner);
        }
    }

    public static class Post extends CommandRunEvent {
        public Post(Message message, CommandRunner runner) {
            super(message, runner);
        }
    }
}
