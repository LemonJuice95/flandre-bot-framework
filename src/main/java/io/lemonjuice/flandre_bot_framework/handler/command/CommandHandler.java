package io.lemonjuice.flandre_bot_framework.handler.command;

import io.lemonjuice.flandre_bot_framework.command.BotCommandLookup;
import io.lemonjuice.flandre_bot_framework.command.CommandRunner;
import io.lemonjuice.flandre_bot_framework.event.BotEventBus;
import io.lemonjuice.flandre_bot_framework.event.msg.CommandRunEvent;
import io.lemonjuice.flandre_bot_framework.event.msg.PermissionDeniedEvent;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;

import java.util.function.Function;

public abstract class CommandHandler {
    public abstract void handle(Message message);

    protected void handleGroupCommand(Message message) {
        for(Function<Message, CommandRunner> provider : BotCommandLookup.GROUP_COMMANDS) {
            CommandRunner runner = provider.apply(message);
            if(runner.matches()) {
                boolean run = true;
                if(!runner.getPermissionLevel().validatePermission(message)) {
                    run = BotEventBus.postCancelable(new PermissionDeniedEvent(message, runner));
                }
                if(run && !BotEventBus.postCancelable(new CommandRunEvent.Pre(message, runner))) {
                    runner.apply();
                    BotEventBus.post(new CommandRunEvent.Post(message, runner));
                }
                if(runner.blockAfterCommands()) {
                    break;
                }
            }
        }
    }

    protected void handlePrivateCommand(Message message) {
        if(message.subType.equals("friend")) {
            for (Function<Message, CommandRunner> providerFriend : BotCommandLookup.FRIEND_COMMANDS) {
                CommandRunner runner = providerFriend.apply(message);
                if(runner.matches()) {
                    boolean run = true;
                    if(!isGroupPermission(runner.getPermissionLevel()) &&
                            !runner.getPermissionLevel().validatePermission(message)) {
                        run = BotEventBus.postCancelable(new PermissionDeniedEvent(message, runner));
                    }

                    if(run && !BotEventBus.postCancelable(new CommandRunEvent.Pre(message, runner))) {
                        runner.apply();
                        BotEventBus.post(new CommandRunEvent.Post(message, runner));
                    }

                    if(runner.blockAfterCommands()) {
                        return;
                    }
                }
            }
        }
        for(Function<Message, CommandRunner> providerNormal : BotCommandLookup.PRIVATE_COMMANDS) {
            CommandRunner runner = providerNormal.apply(message);
            if(runner.matches()) {
                boolean run = true;

                if(!isGroupPermission(runner.getPermissionLevel()) &&
                        !runner.getPermissionLevel().validatePermission(message)) {
                    run = BotEventBus.postCancelable(new PermissionDeniedEvent(message, runner));
                }

                if(run && !BotEventBus.postCancelable(new CommandRunEvent.Pre(message, runner))) {
                    runner.apply();
                    BotEventBus.post(new CommandRunEvent.Post(message, runner));
                }

                if(runner.blockAfterCommands()) {
                    return;
                }
            }
        }
    }

    protected boolean isGroupPermission(IPermissionLevel level) {
        return level == PermissionLevel.OWNER || level == PermissionLevel.ADMIN;
    }
}
