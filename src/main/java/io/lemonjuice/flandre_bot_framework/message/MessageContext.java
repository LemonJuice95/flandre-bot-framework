package io.lemonjuice.flandre_bot_framework.message;

import lombok.Getter;

import java.util.List;

@Getter
public class MessageContext implements IMessageContext {
    private long botId = -1;
    private long messageId = -1;

    public MessageContext() {
    }

    @Override
    public void sendText(String message, boolean sendAsRawText) {
        throw new UnsupportedOperationException("不支持的消息发送操作");
    }

    @Override
    public void sendForwardText(List<String> messages) {
        throw new UnsupportedOperationException("不支持的消息发送操作");
    }

    public MessageContext withBotId(long botId) {
        this.botId = botId;
        return this;
    }

    public MessageContext withMessageId(long messageId) {
        this.messageId = messageId;
        return this;
    }
}
