package io.lemonjuice.flandre_bot_framework.command.group;

import io.lemonjuice.flandre_bot_framework.command.interfaces.IPatternCommand;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessageMatcher;
import io.lemonjuice.flandre_bot_framework.model.Message;

/**
 * 使用MessagePattern进行匹配的群聊命令执行器
 */
public abstract class GroupPatternCommandRunner extends GroupCommandRunner implements IPatternCommand {
    protected final MessageMatcher matcher;

    public GroupPatternCommandRunner(Message command) {
        super(command);
        this.matcher = this.getPattern().matcher(command);
    }

    @Override
    public boolean matches() {
        return this.matcher.matches();
    }
}
