package io.lemonjuice.flandre_bot_framework.handler.command;

import io.lemonjuice.flandre_bot_framework.model.Message;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Log4j2
public class SyncCommandHandler extends CommandHandler {
    private final BlockingQueue<Message> messages = new LinkedBlockingQueue<>();
    @Getter
    private final Thread executorThread = new Thread(this::executingLoop, "Sync-Command-Handler");

    @Override
    public void handle(Message message) {
        try {
            this.messages.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("消息入队失败", e);
        }
    }

    public void executingLoop() {
        while(true) {
            try {
                Message message = messages.take();
                if(message.type.equals("group")) {
                    this.handleGroupCommand(message);
                } else if(message.type.equals("private")) {
                    this.handlePrivateCommand(message);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

}
