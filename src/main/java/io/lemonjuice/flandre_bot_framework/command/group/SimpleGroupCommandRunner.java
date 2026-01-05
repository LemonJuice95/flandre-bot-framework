package io.lemonjuice.flandre_bot_framework.command.group;

import io.lemonjuice.flandre_bot_framework.model.Message;

/**
 * 群聊下简单命令的执行器（仅匹配完整命令体）
 */
public abstract class SimpleGroupCommandRunner extends GroupCommandRunner {
    public SimpleGroupCommandRunner(Message command) {
        super(command);
    }

    /**
     * @return 是否需要在命令开头@bot
     */
    protected abstract boolean needAtFirst();

    /**
     * @return 命令体字符串
     */
    protected abstract String getCommandBody();

    @Override
    public boolean matches() {
        if(this.needAtFirst()) {
            return this.command.message.equalsAtBotWithText(this.getCommandBody());
        }
        return this.command.message.equalsText(this.getCommandBody());
    }
}
