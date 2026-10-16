package io.lemonjuice.flandre_bot_framework.handler.command;

import io.lemonjuice.flandre_bot_framework.command.BotCommandLookup;
import io.lemonjuice.flandre_bot_framework.command.CommandRunner;
import io.lemonjuice.flandre_bot_framework.command.CommandRunnerSlot;
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
        boolean returnRunner = true;
        for(CommandRunnerSlot slot : BotCommandLookup.GROUP_COMMANDS) {
            CommandRunner runner = slot.getPool().take(message);
            returnRunner = true;
            if(runner == null) {
                runner = slot.provider.apply(message);
                returnRunner = false;
            }
            try {
                if (runner.matches()) {
                    boolean run = true;
                    if (!runner.getPermissionLevel().validatePermission(message)) {
                        run = BotEventBus.postCancelable(new PermissionDeniedEvent(message, runner));
                    }
                    if (run && !BotEventBus.postCancelable(new CommandRunEvent.Pre(message, runner))) {
                        runner.apply();
                        BotEventBus.post(new CommandRunEvent.Post(message, runner));
                    }
                    if (runner.blockAfterCommands()) {
                        return;
                    }
                }
            } finally {
                if(returnRunner) {
                    slot.getPool().put(runner);
                }
            }
        }
    }

    protected void handlePrivateCommand(Message message) {
        boolean returnRunner = true;
        if(message.subType.equals("friend")) {
            for (CommandRunnerSlot slotFriend : BotCommandLookup.FRIEND_COMMANDS) {
                CommandRunner runner = slotFriend.getPool().take(message);
                returnRunner = true;
                if(runner == null) {
                    runner = slotFriend.provider.apply(message);
                    returnRunner = false;
                }
                try {
                    if (runner.matches()) {
                        boolean run = true;
                        if (!isGroupPermission(runner.getPermissionLevel()) &&
                                !runner.getPermissionLevel().validatePermission(message)) {
                            run = BotEventBus.postCancelable(new PermissionDeniedEvent(message, runner));
                        }

                        if (run && !BotEventBus.postCancelable(new CommandRunEvent.Pre(message, runner))) {
                            runner.apply();
                            BotEventBus.post(new CommandRunEvent.Post(message, runner));
                        }
                        if (runner.blockAfterCommands()) {
                            return;
                        }
                    }
                } finally {
                    if(returnRunner) {
                        slotFriend.getPool().put(runner);
                    }
                }
            }
        }
        for(CommandRunnerSlot slotNormal : BotCommandLookup.PRIVATE_COMMANDS) {
            CommandRunner runner = slotNormal.getPool().take(message);
            returnRunner = true;
            if(runner == null) {
                runner = slotNormal.provider.apply(message);
                returnRunner = false;
            }
            try {
                if (runner.matches()) {
                    boolean run = true;

                    if (!isGroupPermission(runner.getPermissionLevel()) &&
                            !runner.getPermissionLevel().validatePermission(message)) {
                        run = BotEventBus.postCancelable(new PermissionDeniedEvent(message, runner));
                    }

                    if (run && !BotEventBus.postCancelable(new CommandRunEvent.Pre(message, runner))) {
                        runner.apply();
                        BotEventBus.post(new CommandRunEvent.Post(message, runner));
                    }

                    if (runner.blockAfterCommands()) {
                        return;
                    }
                }
            } finally {
                if(returnRunner) {
                    slotNormal.getPool().put(runner);
                }
            }
        }
    }

    protected boolean isGroupPermission(IPermissionLevel level) {
        return level == PermissionLevel.OWNER || level == PermissionLevel.ADMIN;
    }
}
