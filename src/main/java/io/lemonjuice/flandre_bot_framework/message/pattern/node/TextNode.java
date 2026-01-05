package io.lemonjuice.flandre_bot_framework.message.pattern.node;

import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;

import java.util.Objects;

public class TextNode extends MessagePatternNode {
    public TextNode(String text) {
        super((segment) -> {
            if(segment instanceof TextMessageSegment textSeg) {
                return Objects.equals(text, textSeg.getContent());
            }
            return false;
        });
    }
}
