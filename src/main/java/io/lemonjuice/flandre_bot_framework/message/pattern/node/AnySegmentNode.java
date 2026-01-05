package io.lemonjuice.flandre_bot_framework.message.pattern.node;

public class AnySegmentNode extends MessagePatternNode {
    public AnySegmentNode() {
        super((segment) -> true);
    }
}
