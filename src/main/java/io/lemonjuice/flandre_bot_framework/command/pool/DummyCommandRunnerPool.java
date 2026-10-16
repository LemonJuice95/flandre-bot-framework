package io.lemonjuice.flandre_bot_framework.command.pool;

import io.lemonjuice.flandre_bot_framework.command.CommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;

public class DummyCommandRunnerPool implements ICommandRunnerPool {
    @Override
    public CommandRunner take(Message message) {
        return null;
    }

    @Override
    public void put(CommandRunner runner) {
    }
}
