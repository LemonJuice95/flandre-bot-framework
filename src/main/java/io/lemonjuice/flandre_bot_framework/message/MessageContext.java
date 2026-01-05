package io.lemonjuice.flandre_bot_framework.message;

import io.lemonjuice.flandre_bot_framework.account.AccountInfo;
import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.ReplyMessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;
import lombok.Getter;

import java.util.List;

@Getter
public class MessageContext implements IMessageContext {
    protected long botId;
    protected long messageId = -1;
    protected long userId = -1;

    public MessageContext() {
        this.botId = AccountInfo.getBotId();
    }

    @Override
    public void sendMessage(List<MessageSegment> segments) {
        throw new UnsupportedOperationException("不支持的消息发送操作");
    }

    @Override
    public void replyWithText(String message) {
        if(this.messageId != -1) {
            this.sendMessage(List.of(new ReplyMessageSegment(this.messageId), new TextMessageSegment(message)));
        } else {
            throw new UnsupportedOperationException("不支持的消息发送操作");
        }
    }

    @Override
    public void sendText(String message) {
        throw new UnsupportedOperationException("不支持的消息发送操作");
    }

    @Override
    public void sendForwardMessage(List<List<MessageSegment>> messages) {
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

    public MessageContext withUserId(long userId) {
        this.userId = userId;
        return this;
    }
}
