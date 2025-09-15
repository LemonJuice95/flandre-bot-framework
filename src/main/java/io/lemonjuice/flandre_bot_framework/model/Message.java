package io.lemonjuice.flandre_bot_framework.model;

import io.lemonjuice.flandre_bot_framework.message.*;
import lombok.Getter;

public class Message {
    public static final Message DUMMY = new Message();

    @Getter
    private IMessageContext context;

    public long selfId;
    public long userId;
    public long groupId;
    public long targetId;

    public int time;
    public int messageId;
    public int realId;
    public String realSeq;
    public String type;
    public String subType;
    public Sender sender;

    public int font;
    public String format;

    public String message;
    public String rawMessage;

    public static class Sender {
        public long userId;
        public String nickName;
        public String card;
        public String role;
    }

    public void generateContext() {
        String detailedType = this.type + "." + this.subType;
        switch (detailedType) {
            case "group.normal" -> {
                this.context = new GroupContext(this.groupId)
                        .withBotId(this.selfId)
                        .withMessageId(this.messageId);
            }

            case "private.friend" -> {
                this.context = new FriendContext(this.userId, this.sender.nickName)
                        .withBotId(this.selfId)
                        .withMessageId(this.messageId);
            }

            case "private.group" -> {
                this.context = new TempSessionContext(this.userId, this.groupId)
                        .withBotId(this.selfId)
                        .withMessageId(this.messageId);
            }

            default -> {
                this.context = new MessageContext().withBotId(this.selfId).withMessageId(this.messageId);
            }
        }
    }

    static {
        DUMMY.context = new MessageContext();
        DUMMY.realSeq = "";
        DUMMY.type = "";
        DUMMY.subType = "";
        DUMMY.format = "";
        DUMMY.message = "";
        DUMMY.rawMessage = "";

        Sender dummySender = new Sender();
        dummySender.nickName = "";
        dummySender.card = "";
        dummySender.role = "";

        DUMMY.sender = dummySender;
    }
}