package io.lemonjuice.flandre_bot_framework.message.segment;

import org.json.JSONObject;

/**
 * 猜拳表情消息段
 */
public class RPSMessageSegment extends MessageSegment {
    public RPSMessageSegment() {
        super("rps");
    }

    public RPSMessageSegment(JSONObject msgData) {
        super("rps");
    }

    @Override
    public String toString() {
        return "[猜拳表情]";
    }

    @Override
    public JSONObject serializeMsgData() {
        return new JSONObject();
    }
}
