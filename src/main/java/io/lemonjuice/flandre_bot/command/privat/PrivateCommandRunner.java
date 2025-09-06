package io.lemonjuice.flandre_bot.command.privat;

import io.lemonjuice.flandre_bot.command.CommandRunner;
import io.lemonjuice.flandre_bot.model.Message;

public abstract class PrivateCommandRunner extends CommandRunner {
    public PrivateCommandRunner(Message message) {
        super(message);
    }

    @Override
    public Type getType() {
        return this.needBeFriends() ? Type.FRIEND : Type.PRIVATE;
    }

    /**
     * @return 是否需要添加bot为好友
     */
    public abstract boolean needBeFriends();
}
