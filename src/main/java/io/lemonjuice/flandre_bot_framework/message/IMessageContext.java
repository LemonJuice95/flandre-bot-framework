package io.lemonjuice.flandre_bot_framework.message;

import io.lemonjuice.flandre_bot_framework.message.segment.ImageMessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * 消息的部分上下文
 * 同时包装了快捷发送的逻辑
 */
public interface IMessageContext {
    /**
     * 准备一个消息构建器
     * @return 消息构建器
     */
    default public MessageToSend prepareMessageToSend() {
        return new MessageToSend(this);
    }

    public void sendMessage(List<MessageSegment> segments);

    /**
     * 回复一条消息
     * @param message 消息内容
     */
    public void replyWithText(String message);

    /**
     * 发送文本消息
     * @param message 消息内容
     */
    public void sendText(String message);

    /**
     * 发送图片
     * @param image 图片
     */
    default public void sendImage(BufferedImage image, String formatName) {
        this.sendMessage(List.of(new ImageMessageSegment(image, formatName)));
    }

    /**
     * 发送合并消息<br>
     * <b>（非OneBot标准API，实现端可能不支持）</b><br>
     * （目前已知支持的实现端: NapCat）
     * @param messages 消息内容
     */
    public void sendForwardMessage(List<List<MessageSegment>> messages);
}
