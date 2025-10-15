package io.lemonjuice.flandre_bot_framework.handler;

import io.lemonjuice.flandre_bot_framework.handler.command.CommandHandler;
import io.lemonjuice.flandre_bot_framework.model.Message;

public class ASyncCommandHandler extends CommandHandler {
    @Override
    public void handle(Message message) {
        if(message.type.equals("group")) {
            Thread.startVirtualThread(() -> this.handleGroupCommand(message));
        } else if(message.type.equals("private")) {
            Thread.startVirtualThread(() -> this.handlePrivateCommand(message));
        }
    }
}
