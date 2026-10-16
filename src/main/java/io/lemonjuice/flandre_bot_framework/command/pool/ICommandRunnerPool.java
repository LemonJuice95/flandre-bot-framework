package io.lemonjuice.flandre_bot_framework.command.pool;

import io.lemonjuice.flandre_bot_framework.command.CommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;

public interface ICommandRunnerPool {
    public CommandRunner take(Message message);
    public void put(CommandRunner runner);
}
