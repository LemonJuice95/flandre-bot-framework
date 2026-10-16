package io.lemonjuice.flandre_bot_framework.command;

import io.lemonjuice.flandre_bot_framework.command.pool.CommandRunnerPool;
import io.lemonjuice.flandre_bot_framework.command.pool.DummyCommandRunnerPool;
import io.lemonjuice.flandre_bot_framework.command.pool.ICommandRunnerPool;
import io.lemonjuice.flandre_bot_framework.model.Message;
import lombok.Getter;

import java.util.function.Function;

public class CommandRunnerSlot {
    private static final DummyCommandRunnerPool DUMMY_POOL = new DummyCommandRunnerPool();

    public final Class<? extends CommandRunner> runnerClass;
    public final Function<Message, CommandRunner> provider;

    @Getter
    private volatile ICommandRunnerPool pool = DUMMY_POOL;

    public CommandRunnerSlot(Class<? extends CommandRunner> runnerClass, Function<Message, CommandRunner> provider) {
        this.runnerClass = runnerClass;
        this.provider = provider;
    }

    public void useDummyPool() {
        this.pool = DUMMY_POOL;
    }

    public void usePool(int initialCapacity, int maximumCapacity, Function<Message, CommandRunner> provider) {
        this.pool = new CommandRunnerPool(initialCapacity, maximumCapacity, provider);
    }
}
