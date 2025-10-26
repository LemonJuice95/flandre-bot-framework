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

    /**
     * 推送时机: 命令执行器匹配成功后执行前<br>
     * 可进行命令执行的拦截
     */
    public static class Pre extends CommandRunEvent implements ICancelableEvent {
        public Pre(Message message, CommandRunner runner) {
            super(message, runner);
        }
    }

    /**
     * 推送时机: 命令执行器执行完毕后
     */
    public static class Post extends CommandRunEvent {
        public Post(Message message, CommandRunner runner) {
            super(message, runner);
        }
    }
}
