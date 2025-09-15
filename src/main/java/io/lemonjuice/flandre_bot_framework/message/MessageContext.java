package io.lemonjuice.flandre_bot_framework.message;

import io.lemonjuice.flandre_bot_framework.account.AccountInfo;
import io.lemonjuice.flandre_bot_framework.utils.CQCode;
import lombok.Getter;

import java.util.List;

@Getter
public class MessageContext implements IMessageContext {
    private long botId;
    private long messageId = -1;

    public MessageContext() {
        this.botId = AccountInfo.getBotId();
    }

    @Override
    public void replyWithText(String message) {
        if(this.messageId != -1) {
            this.sendText(CQCode.reply(this.messageId) + message);
        } else {
            throw new UnsupportedOperationException("不支持的消息发送操作");
        }
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
