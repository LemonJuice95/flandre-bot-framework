package io.lemonjuice.flandre_bot_framework.message;

import lombok.Getter;

import java.util.List;

public class MessageContext implements IMessageContext {
    @Getter
    private final long botId;

    public MessageContext(long botId) {
        this.botId = botId;
    }

    @Override
    public void sendText(String message, boolean sendAsRawText) {
        throw new UnsupportedOperationException("不支持的消息发送操作");
    }

    @Override
    public void sendForwardText(List<String> messages) {
        throw new UnsupportedOperationException("不支持的消息发送操作");
    }
}
