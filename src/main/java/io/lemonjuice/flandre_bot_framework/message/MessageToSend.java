package io.lemonjuice.flandre_bot_framework.message;

import io.lemonjuice.flandre_bot_framework.utils.CQCode;

import java.awt.image.BufferedImage;
import java.io.File;

public class MessageToSend {
    private final IMessageContext context;
    private final StringBuilder msgBuilder = new StringBuilder();

    public MessageToSend(IMessageContext context) {
        this.context = context;
    }

    /**
     * 消息换行
     * @return 构建中的消息
     */
    public MessageToSend newLine() {
        this.msgBuilder.append("\n");
        return this;
    }

    /**
     * 添加一个"@用户"
     * @param uid 用户qq号
     * @return 构建中的消息
     */
    public MessageToSend appendAt(long uid) {
        this.msgBuilder.append(CQCode.at(uid));
        return this;
    }

    /**
     * 添加一串文本
     * @param text 文本内容
     * @return 构建中的消息
     */
    public MessageToSend appendText(String text) {
        this.msgBuilder.append(text);
        return this;
    }

    /**
     * 添加一张图片
     * @param image 图片
     * @return 构建中的消息
     */
    public MessageToSend appendImage(BufferedImage image) {
        this.msgBuilder.append(CQCode.image(image));
        return this;
    }

    /**
     * 添加一张图片
     * @param file 图片文件
     * @return 构建中的消息
     */
    public MessageToSend appendImage(File file) {
        this.msgBuilder.append(CQCode.image(file));
        return this;
    }

    /**
     * 添加一张图片
     * @param url 图片URL
     * @return 构建中的消息
     */
    public MessageToSend appendImage(String url) {
        this.msgBuilder.append(CQCode.image(url));
        return this;
    }

    /**
     * 发送消息
     */
    public void send() {
        this.context.sendText(this.msgBuilder.toString());
    }

    /**
     * 发送消息（回复触发本条消息的消息）
     */
    public void reply() {
        this.context.replyWithText(this.msgBuilder.toString());
    }
}
