package io.lemonjuice.flandre_bot.command.privat;

import io.lemonjuice.flandre_bot.model.Message;

/**
 * 私聊下简单命令的执行器（仅匹配完整命令体）
 */
public abstract class SimplePrivateCommandRunner extends PrivateCommandRunner {
    public SimplePrivateCommandRunner(Message command) {
        super(command);
    }

    /**
     * @return 命令体字符串
     */
    protected abstract String getCommandBody();

    @Override
    public boolean matches() {
        return this.command.message.trim().equals(this.getCommandBody());
    }
}
