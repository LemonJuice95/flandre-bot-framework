package io.lemonjuice.flandre_bot_framework.model;

import io.lemonjuice.flandre_bot_framework.message.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Message {
    public static final Message DUMMY = new Message.Builder().build();

    @Getter
    private IMessageContext context;

    public final long selfId;
    public final long userId;
    public final long groupId;
    public final long targetId;

    public final long time;
    public final int messageId;
    public final int realId;
    public final String realSeq;
    public final String type;
    public final String subType;
    public final Sender sender;

    public final int font;
    public final String format;

    public final String message;
    public final String rawMessage;

    @AllArgsConstructor
    public static class Sender {
        public final long userId;
        public final String nickName;
        public final String card;
        public final String role;
    }

    private void generateContext() {
        String detailedType = this.type + "." + this.subType;
        switch (detailedType) {
            case "group.normal" -> {
                this.context = new GroupContext(this.groupId)
                        .withBotId(this.selfId)
                        .withMessageId(this.messageId)
                        .withUserId(this.userId);
            }

            case "private.friend" -> {
                this.context = new FriendContext(this.userId, this.sender.nickName)
                        .withBotId(this.selfId)
                        .withMessageId(this.messageId)
                        .withUserId(this.userId);
            }

            case "private.group" -> {
                this.context = new TempSessionContext(this.userId, this.groupId)
                        .withBotId(this.selfId)
                        .withMessageId(this.messageId)
                        .withUserId(this.userId);
            }

            default -> {
                this.context = new MessageContext()
                        .withBotId(this.selfId)
                        .withMessageId(this.messageId)
                        .withUserId(this.userId);
            }
        }
    }

    public static class Builder {
        public long selfId = -1;
        public long userId = -1;
        public long groupId = -1;
        public long targetId = -1;

        public long time = 0;
        public int messageId = -1;
        public int realId = -1;
        public String realSeq = "";
        public String type = "";
        public String subType = "";
        public Sender sender = new Sender(-1, "", "", "");

        public int font = 0;
        public String format = "";

        public String message = "";
        public String rawMessage = "";

        public Builder selfId(long selfId) {
            this.selfId = selfId;
            return this;
        }

        public Builder userId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder groupId(long groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder targetId(long targetId) {
            this.targetId = targetId;
            return this;
        }

        public Builder time(long time) {
            this.time = time;
            return this;
        }

        public Builder messageId(int messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder realId(int realId) {
            this.realId = realId;
            return this;
        }

        public Builder realSeq(String realSeq) {
            this.realSeq = realSeq;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder subType(String subType) {
            this.subType = subType;
            return this;
        }

        public Builder sender(Sender sender) {
            this.sender = sender;
            return this;
        }

        public Builder font(int font) {
            this.font = font;
            return this;
        }

        public Builder format(String format) {
            this.format = format;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder rawMessage(String rawMessage) {
            this.rawMessage = rawMessage;
            return this;
        }

        public Message build() {
            Message result = new Message(
                    this.selfId,
                    this.userId,
                    this.groupId,
                    this.targetId,
                    this.time,
                    this.messageId,
                    this.realId,
                    this.realSeq,
                    this.type,
                    this.subType,
                    this.sender,
                    this.font,
                    this.format,
                    this.message,
                    this.rawMessage
            );
            result.generateContext();
            return result;
        }
    }
}