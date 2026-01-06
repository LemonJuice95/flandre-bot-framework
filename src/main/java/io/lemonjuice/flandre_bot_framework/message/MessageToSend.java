package io.lemonjuice.flandre_bot_framework.message;

import io.lemonjuice.flandre_bot_framework.message.segment.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MessageToSend {
    private final IMessageContext context;
    private final List<MessageSegment> segments;

    public MessageToSend(IMessageContext context) {
        this.context = context;
        this.segments = new ArrayList<>();
    }

    /**
     * 添加一个消息段
     * @param segment 消息段
     * @return 构建中的消息
     */
    public MessageToSend appendSegment(MessageSegment segment) {
        this.segments.add(segment);
        return this;
    }

    /**
     * 添加回复消息段（使用上下文中的消息id）
     * @return 构建中的消息
     */
    public MessageToSend appendReply() {
        if(this.context instanceof MessageContext ctx) {
            if(ctx.messageId != -1) {
                this.segments.add(new ReplyMessageSegment(ctx.messageId));
                return this;
            }
        }
        throw new IllegalStateException("此消息上下文不包含消息id");
    }

    /**
     * 添加回复消息段
     * @param messageId 消息id
     * @return 构建中的消息
     */
    public MessageToSend appendReply(long messageId) {
        this.segments.add(new ReplyMessageSegment(messageId));
        return this;
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
        this.context.sendMessage(this.segments);
    }
}
