package io.lemonjuice.flandre_bot_framework.command.group;

import io.lemonjuice.flandre_bot_framework.command.interfaces.IPatternCommand;
import io.lemonjuice.flandre_bot_framework.command.annotations.Resettable;
import io.lemonjuice.flandre_bot_framework.message.MessageSegmentList;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessageMatcher;
import io.lemonjuice.flandre_bot_framework.model.Message;

/**
 * 使用MessagePattern进行匹配的群聊命令执行器
 */
public abstract class GroupPatternCommandRunner extends GroupCommandRunner implements IPatternCommand {
    @Resettable
    protected final MessageMatcher matcher;

    public GroupPatternCommandRunner(Message command) {
        super(command);
        this.matcher = this.getPattern().matcher(command);
    }

    @Override
    public boolean matches() {
        return this.matcher.matches();
    }

    @Override
    public void free() {
        super.free();
        this.matcher.reset(MessageSegmentList.EMPTY);
    }

    @Override
    public void reset(Message command) {
        super.reset(command);
        this.matcher.reset(command.message);
    }
}
