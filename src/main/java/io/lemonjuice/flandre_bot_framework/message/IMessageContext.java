package io.lemonjuice.flandre_bot_framework.message;

import io.lemonjuice.flandre_bot_framework.utils.CQCode;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * 消息的部分上下文
 * 同时包装了快捷发送的逻辑
 */
public interface IMessageContext {
    /**
     * 回复一条消息
     * @param message 消息内容
     */
    public void replyWithText(String message);

    /**
     * 发送文本消息
     * @param message 消息内容
     */
    default public void sendText(String message) {
        this.sendText(message, false);
    }

    /**
     * 发送文本消息
     * @param message 消息内容
     * @param sendAsRawText 是否不对消息内容进行转义
     */
    public void sendText(String message, boolean sendAsRawText);

    /**
     * 发送图片
     * @param image 图片
     */
    default public void sendImage(BufferedImage image) {
        this.sendText(CQCode.image(image));
    }

    /**
     * 发送合并消息<br>
     * <b>（非OneBot标准API，实现端可能不支持）</b><br>
     * （目前已知支持的实现端: NapCat）
     * @param messages 消息内容
     */
    public void sendForwardText(List<String> messages);
}
