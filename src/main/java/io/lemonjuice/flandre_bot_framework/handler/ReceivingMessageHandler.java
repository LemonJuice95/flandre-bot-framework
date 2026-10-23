package io.lemonjuice.flandre_bot_framework.handler;

import io.lemonjuice.flandre_bot_framework.account.ContextManager;
import io.lemonjuice.flandre_bot_framework.config.BasicConfigReloader;
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

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class ReceivingMessageHandler {
    private static volatile CommandHandler commandHandler = new AsyncCommandHandler();
    private static final AtomicBoolean reloading = new AtomicBoolean(false);
    private static final ConcurrentLinkedQueue<Message> storedMessages = new ConcurrentLinkedQueue<>();

    public synchronized static void init() {
        if(BotBasicConfig.COMMAND_SYNC_MODE.get()) {
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

    public static void reload() {
        if(!BotBasicConfig.COMMAND_SYNC_MODE.changed()) {
            return;
        }

        synchronized (BasicConfigReloader.class) {
            reloading.set(true);
            try {
                if (commandHandler instanceof SyncCommandHandler syncHandler) {
                    syncHandler.getExecutorThread().interrupt();
                }
                if (BotBasicConfig.COMMAND_SYNC_MODE.get()) {
                    commandHandler = new SyncCommandHandler();
                    ((SyncCommandHandler) commandHandler).getExecutorThread().start();
                } else {
                    commandHandler = new AsyncCommandHandler();
                }
            } finally {
                reloading.set(false);
                while (!storedMessages.isEmpty()) {
                    commandHandler.handle(storedMessages.poll());
                }
            }
        }
    }

    public static void handle(JSONObject json) {
        Message message = MessageParser.parseMessage(json);
        if(message == null) {
            return;
        }

        //TODO 增加屏蔽部分来源的消息的功能
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

        if(reloading.get()) {
            storedMessages.add(message);
        } else {
            commandHandler.handle(message);
        }
    }

    private static void logMessage(Message message) {
        String from = "未知来源";
        String nickName = !message.sender.card.isEmpty() ? message.sender.card : message.sender.nickName;
        String messageContent = message.message.toString();

        if(message.type.equals("private")) {
            from = String.format("私聊(%d)", message.userId);
        }
        if(message.type.equals("group")) {
            GroupContext context = ContextManager.getGroup(message.groupId);
            String groupName = String.format("[%s]", context.getGroupName());
            from = String.format("群聊%s(%d)", groupName, message.groupId);
        }

        log.info("消息接收 来自{}: [{}]: {}", from, nickName, messageContent);
    }
}
