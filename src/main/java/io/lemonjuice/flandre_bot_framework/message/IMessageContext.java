package io.lemonjuice.flandre_bot_framework.message;

import java.util.List;

/**
 * 消息的部分上下文
 * 同时包装了快捷发送的逻辑
 */
public interface IMessageContext {
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
     * 发送合并消息
     * @param messages 消息内容
     */
    public void sendForwardText(List<String> messages);
}
