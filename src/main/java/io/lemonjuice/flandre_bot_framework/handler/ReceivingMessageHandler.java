package io.lemonjuice.flandre_bot_framework.handler;

import io.lemonjuice.flandre_bot_framework.account.ContextManager;
import io.lemonjuice.flandre_bot_framework.command.BotCommandLookup;
import io.lemonjuice.flandre_bot_framework.command.CommandRunner;
import io.lemonjuice.flandre_bot_framework.config.BotBasicConfig;
import io.lemonjuice.flandre_bot_framework.event.BotEventBus;
import io.lemonjuice.flandre_bot_framework.event.msg.CommandRunEvent;
import io.lemonjuice.flandre_bot_framework.event.msg.MessageEvent;
import io.lemonjuice.flandre_bot_framework.event.msg.PermissionDeniedEvent;
import io.lemonjuice.flandre_bot_framework.message.GroupContext;
import io.lemonjuice.flandre_bot_framework.message.IMessageContext;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;
import io.lemonjuice.flandre_bot_framework.plugins.BotPlugin;
import io.lemonjuice.flandre_bot_framework.utils.MessageParser;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import java.util.function.Function;

@Log4j2
public class ReceivingMessageHandler {
    private static final boolean SYNC_MODE = BotBasicConfig.COMMAND_SYNC_MODE.get();

    public static void handle(JSONObject json) {
        Message message = MessageParser.parseMessage(json);
        if(message == null) {
            return;
        }

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

            if (message.subType.equals("friend")) {
                BotEventBus.post(new MessageEvent.Friend(message));
            } else if (message.subType.equals("group")) {
                BotEventBus.post(new MessageEvent.TempSession(message));
            }
        }
    }

    private static void logMessage(Message message) {
        String from = "未知来源";
        String nickName = !message.sender.card.isEmpty() ? message.sender.card : message.sender.nickName;
        String messageContent = message.message;

        if(message.type.equals("private")) {
            from = String.format("私聊(%d)", message.userId);
        }
        if(message.type.equals("group")) {
            GroupContext context = ContextManager.getGroup(message.groupId);
            String groupName = context.getGroupName().isEmpty() ? "" : String.format("[%s]", context.getGroupName());
            from = String.format("群聊%s(%d)", groupName, message.groupId);
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
                if(!isGroupPermission(runner.getPermissionLevel()) &&
                        !runner.getPermissionLevel().validatePermission(message)) {
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

    private static boolean isGroupPermission(IPermissionLevel level) {
        return level == PermissionLevel.OWNER || level == PermissionLevel.ADMIN;
    }

}
