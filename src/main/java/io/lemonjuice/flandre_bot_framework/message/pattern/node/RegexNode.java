package io.lemonjuice.flandre_bot_framework.message.pattern.node;

import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexNode extends MessagePatternNode {
    public RegexNode(Pattern regex, boolean trim) {
        super(segment -> {
           if(segment instanceof TextMessageSegment textSeg) {
               Matcher matcher = regex.matcher(trim ? textSeg.getContent().trim() : textSeg.getContent());
               return matcher.matches();
           }
           return false;
        });
    }

    public RegexNode(Pattern regex) {
        this(regex, true);
    }
}
