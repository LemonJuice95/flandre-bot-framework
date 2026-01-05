package io.lemonjuice.flandre_bot_framework.message.pattern.node;

import io.lemonjuice.flandre_bot_framework.account.AccountInfo;
import io.lemonjuice.flandre_bot_framework.message.segment.AtMessageSegment;

public class AtNode extends MessagePatternNode {
    public AtNode(long qq) {
        super(segment -> {
            if(segment instanceof AtMessageSegment atSeg) {
                return atSeg.getQQ() == qq;
            }
            return false;
        });
    }

    public static AtNode atBot() {
        return new AtNode(AccountInfo.getBotId());
    }
}
