package io.lemonjuice.flandre_bot.message;

import java.util.List;

public interface IMessageContext {
    default public void sendText(String message) {
        this.sendText(message, false);
    }

    public void sendText(String message, boolean sendAsRawText);

    public void sendForwardText(List<String> messages);
}
