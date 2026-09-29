package io.lemonjuice.flandre_bot_framework.message.pattern;

import io.lemonjuice.flandre_bot_framework.message.MessageSegmentList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiMessagePattern {
    private final List<MessagePattern> patterns;

    public MultiMessagePattern(List<MessagePattern> patterns) {
        this.patterns = patterns;
    }

    public MultiMessagePattern(MessagePattern... patterns) {
        this.patterns = Arrays.asList(patterns);
    }

    public MultiMessageMatcher matcher(MessageSegmentList segments) {
        return new MultiMessageMatcher(this.patterns, segments);
    }
}
