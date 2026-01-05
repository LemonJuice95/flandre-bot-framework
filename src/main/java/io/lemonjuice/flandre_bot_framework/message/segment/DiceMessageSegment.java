package io.lemonjuice.flandre_bot_framework.message.segment;

import org.json.JSONObject;

/**
 * 骰子表情消息段
 */
public class DiceMessageSegment extends MessageSegment {
    public DiceMessageSegment() {
        super("dice");
    }

    public DiceMessageSegment(JSONObject msgData) {
        super("dice");
    }

    @Override
    public String toString() {
        return "[骰子表情]";
    }

    @Override
    public JSONObject serializeMsgData() {
        return new JSONObject();
    }
}
