package io.lemonjuice.flandre_bot.event.msg;

import io.lemonjuice.flandre_bot.command.CommandRunner;
import io.lemonjuice.flandre_bot.event.Event;
import io.lemonjuice.flandre_bot.event.ICancelableEvent;
import io.lemonjuice.flandre_bot.model.Message;
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
