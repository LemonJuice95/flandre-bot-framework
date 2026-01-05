package io.lemonjuice.flandre_bot_framework.message;

import io.lemonjuice.flandre_bot_framework.message.segment.AtMessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.ImageMessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MessageToSend {
    private final IMessageContext context;
    private final List<MessageSegment> segments;
    private final StringBuilder msgBuilder = new StringBuilder();

    public MessageToSend(IMessageContext context) {
        this.context = context;
        this.segments = new ArrayList<>();
    }

    /**
     * 消息换行
     * @return 构建中的消息
     */
    public MessageToSend newLine() {
        this.appendText("\n");
        return this;
    }

    /**
     * 添加一个"@用户"
     * @param uid 用户qq号
     * @return 构建中的消息
     */
    public MessageToSend appendAt(long uid) {
        this.segments.add(new AtMessageSegment(uid));
        return this;
    }

    /**
     * 添加一串文本
     * @param text 文本内容
     * @return 构建中的消息
     */
    public MessageToSend appendText(String text) {
        if(this.segments.getLast() instanceof TextMessageSegment textSeg) {
            this.segments.removeLast();
            this.segments.add(new TextMessageSegment(textSeg.getContent() + text));
        }
        return this;
    }

    /**
     * 添加一张图片
     * @param image 图片
     * @return 构建中的消息
     */
    public MessageToSend appendImage(BufferedImage image, String formatName) {
        this.segments.add(new ImageMessageSegment(image, formatName, false));
        return this;
    }

    /**
     * 添加一张图片
     * @param file 图片文件
     * @return 构建中的消息
     */
    public MessageToSend appendImage(File file) {
        this.segments.add(new ImageMessageSegment(file));
        return this;
    }

    /**
     * 添加一张图片
     * @param url 图片URL
     * @return 构建中的消息
     */
    public MessageToSend appendImage(String url) {
        this.segments.add(new ImageMessageSegment(url));
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
