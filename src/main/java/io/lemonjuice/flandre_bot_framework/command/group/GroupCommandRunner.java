package io.lemonjuice.flandre_bot_framework.command.group;

import io.lemonjuice.flandre_bot_framework.command.CommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;

public abstract class GroupCommandRunner extends CommandRunner {
    public GroupCommandRunner(Message message) {
        super(message);
    }

    @Override
    public Type getType() {
        return Type.GROUP;
    }
}
