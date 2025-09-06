package io.lemonjuice.flandre_bot.model;

public class Message {
    public static final Message DUMMY = new Message();

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

    static {
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