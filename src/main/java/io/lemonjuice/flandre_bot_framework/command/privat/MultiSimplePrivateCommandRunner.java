package io.lemonjuice.flandre_bot_framework.command.privat;

import io.lemonjuice.flandre_bot_framework.model.Message;

import java.util.Set;

/**
 * 私聊下简单命令的执行器（仅匹配完整命令体）<br>
 * 可匹配多条简单命令
 */
public abstract class MultiSimplePrivateCommandRunner extends PrivateCommandRunner {
    public MultiSimplePrivateCommandRunner(Message command) {
        super(command);
    }

    /**
     * @return 命令体字符串集合
     */
    protected abstract Set<String> getCommandBodies();

    @Override
    public boolean matches() {
        return this.getCommandBodies().contains(this.command.message.trim());
    }
}
