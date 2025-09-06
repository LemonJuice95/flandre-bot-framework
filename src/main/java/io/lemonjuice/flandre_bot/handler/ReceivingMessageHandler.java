package io.lemonjuice.flandre_bot.handler;

import io.lemonjuice.flandre_bot.command.BotCommandLookup;
import io.lemonjuice.flandre_bot.command.CommandRunner;
import io.lemonjuice.flandre_bot.config.BotBasicConfig;
import io.lemonjuice.flandre_bot.event.BotEventBus;
import io.lemonjuice.flandre_bot.event.msg.CommandRunEvent;
import io.lemonjuice.flandre_bot.event.msg.MessageEvent;
import io.lemonjuice.flandre_bot.event.msg.PermissionDeniedEvent;
import io.lemonjuice.flandre_bot.model.Message;
import io.lemonjuice.flandre_bot.permission.PermissionLevel;
import lombok.extern.log4j.Log4j2;

import java.util.function.Function;

@Log4j2
public class ReceivingMessageHandler {
    private static final boolean SYNC_MODE = BotBasicConfig.COMMAND_SYNC_MODE.get();

    public static void handle(Message message) {
        if(BotBasicConfig.LOG_MESSAGES.get()) {
            logMessage(message);
        }

        if(message.type.equals("group")) {
            if(SYNC_MODE) {
                handleGroupCommand(message);
            } else {
                Thread.startVirtualThread(() -> handleGroupCommand(message));
            }
            BotEventBus.post(new MessageEvent.Group(message));
        }

        if(message.type.equals("private")) {
            if(SYNC_MODE) {
                handlePrivateCommand(message);
            } else {
                Thread.startVirtualThread(() -> handlePrivateCommand(message));
            }
            BotEventBus.post(new MessageEvent.Private(message));
        }
    }

    private static void logMessage(Message message) {
        String from = "私聊(";
        String nickName = !message.sender.card.isEmpty() ? message.sender.card : message.sender.nickName;
        String messageContent = message.message;

        if(message.type.equals("private")) {
            from += message.userId + ")";
        }
        if(message.type.equals("group")) {
            from = "群聊(" + message.groupId + ")";
        }

        log.info("消息接收 来自{}: [{}]: {}", from, nickName, messageContent);
    }

    private static void handleGroupCommand(Message message) {
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

    private static void handlePrivateCommand(Message message) {
        if(message.subType.equals("friend")) {
            for (Function<Message, CommandRunner> providerFriend : BotCommandLookup.FRIEND_COMMANDS) {
                CommandRunner runner = providerFriend.apply(message);
                if(runner.matches()) {
                    boolean run = true;
                    if(runner.getPermissionLevel() == PermissionLevel.DEBUG &&
                            !PermissionLevel.DEBUG.validatePermission(message)) {
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
                if(runner.getPermissionLevel() == PermissionLevel.DEBUG &&
                        !PermissionLevel.DEBUG.validatePermission(message)) {
                    BotEventBus.post(new PermissionDeniedEvent(message, runner));
                } else {
                    runner.apply();
                }

                if(runner.blockAfterCommands()) {
                    return;
                }
            }
        }
    }

}
