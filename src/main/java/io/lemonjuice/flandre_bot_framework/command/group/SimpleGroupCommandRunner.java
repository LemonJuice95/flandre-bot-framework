package io.lemonjuice.flandre_bot_framework.command.group;

import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.utils.CQCode;

import java.util.regex.Pattern;

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
            return this.command.message.startsWith(CQCode.at(this.command.selfId)) &&
                    this.command.message.replaceFirst(Pattern.quote(CQCode.at(this.command.selfId)), "")
                            .trim().equals(this.getCommandBody());
        }
        return this.command.message.trim().equals(this.getCommandBody());
    }
}
