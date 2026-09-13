package io.lemonjuice.flandre_bot_framework.command.privat;

import io.lemonjuice.flandre_bot_framework.command.interfaces.IPatternCommand;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessageMatcher;
import io.lemonjuice.flandre_bot_framework.model.Message;

/**
 * 使用MessagePattern进行匹配的私聊命令执行器
 */
public abstract class PrivatePatternCommandRunner extends PrivateCommandRunner implements IPatternCommand {
    protected final MessageMatcher matcher;

    public PrivatePatternCommandRunner(Message command) {
        super(command);
        this.matcher = this.getPattern().matcher(command);
    }

    @Override
    public boolean matches() {
        return this.matcher.matches();
    }
}
