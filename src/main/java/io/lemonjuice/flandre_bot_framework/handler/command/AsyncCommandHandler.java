package io.lemonjuice.flandre_bot_framework.handler.command;

import io.lemonjuice.flandre_bot_framework.model.Message;

public class AsyncCommandHandler extends CommandHandler {
    @Override
    public void handle(Message message) {
        if(message.type.equals("group")) {
            Thread.startVirtualThread(() -> this.handleGroupCommand(message));
        } else if(message.type.equals("private")) {
            Thread.startVirtualThread(() -> this.handlePrivateCommand(message));
        }
    }
}
