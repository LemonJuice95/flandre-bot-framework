package io.lemonjuice.flandre_bot_framework.handler;

import io.lemonjuice.flandre_bot_framework.account.ContextManager;
import io.lemonjuice.flandre_bot_framework.config.BotBasicConfig;
import io.lemonjuice.flandre_bot_framework.event.BotEventBus;
import io.lemonjuice.flandre_bot_framework.event.msg.MessageEvent;
import io.lemonjuice.flandre_bot_framework.handler.command.AsyncCommandHandler;
import io.lemonjuice.flandre_bot_framework.handler.command.CommandHandler;
import io.lemonjuice.flandre_bot_framework.handler.command.SyncCommandHandler;
import io.lemonjuice.flandre_bot_framework.message.GroupContext;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.utils.MessageParser;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

@Log4j2
public class ReceivingMessageHandler {
    private static final boolean SYNC_MODE = BotBasicConfig.COMMAND_SYNC_MODE.get();
    private static volatile CommandHandler commandHandler = new AsyncCommandHandler();

    public synchronized static void init() {
        if(SYNC_MODE) {
            commandHandler = new SyncCommandHandler();
            ((SyncCommandHandler) commandHandler).getExecutorThread().start();
        } else {
            commandHandler = new AsyncCommandHandler();
        }
    }

    public synchronized static void stop() {
        if(commandHandler instanceof SyncCommandHandler syncHandler) {
            syncHandler.getExecutorThread().interrupt();
        }
    }

    public static void handle(JSONObject json) {
        Message message = MessageParser.parseMessage(json);
        if(message == null) {
            return;
        }

        if(BotBasicConfig.LOG_MESSAGES.get()) {
            logMessage(message);
        }

        if(message.type.equals("group")) {
            BotEventBus.post(new MessageEvent.Group(message));
        }

        if(message.type.equals("private")) {
            if (message.subType.equals("friend")) {
                BotEventBus.post(new MessageEvent.Friend(message));
            } else if (message.subType.equals("group")) {
                BotEventBus.post(new MessageEvent.TempSession(message));
            }
        }

        commandHandler.handle(message);
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
}
