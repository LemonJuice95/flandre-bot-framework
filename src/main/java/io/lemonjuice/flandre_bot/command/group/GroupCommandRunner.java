package io.lemonjuice.flandre_bot.command.group;

import io.lemonjuice.flandre_bot.command.CommandRunner;
import io.lemonjuice.flandre_bot.model.Message;

public abstract class GroupCommandRunner extends CommandRunner {
    public GroupCommandRunner(Message message) {
        super(message);
    }

    @Override
    public Type getType() {
        return Type.GROUP;
    }
}
