package io.lemonjuice.flandre_bot_framework.message.pattern.node;

import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;

public class TypedSegmentNode extends MessagePatternNode {
    public TypedSegmentNode(Class<? extends MessageSegment> clazz) {
        super((segment) -> {
            return clazz.equals(segment.getClass());
        });
    }
}
