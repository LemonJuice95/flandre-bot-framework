package io.lemonjuice.flandre_bot_framework.message.pattern.node;

public class NotNode extends MessagePatternNode {
    public NotNode(MessagePatternNode innerNode) {
        super(seg -> {
            return !innerNode.validateCondition(seg);
        });
    }
}
