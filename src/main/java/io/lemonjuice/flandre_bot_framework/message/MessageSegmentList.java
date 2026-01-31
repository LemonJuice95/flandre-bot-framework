package io.lemonjuice.flandre_bot_framework.message;

import io.lemonjuice.flandre_bot_framework.account.AccountInfo;
import io.lemonjuice.flandre_bot_framework.message.segment.AtMessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class MessageSegmentList {
    private final List<MessageSegment> segments;

    public MessageSegmentList() {
        this.segments = new ArrayList<>();
    }

    public MessageSegmentList(List<MessageSegment> segments) {
        this.segments = segments;
    }

    public MessageSegmentList trim() {
        List<MessageSegment> newSegments = new ArrayList<>();
        int start = 0;
        while(start < this.segments.size() &&
                this.segments.get(start) instanceof TextMessageSegment textSeg &&
                textSeg.getContent().isBlank()) {
            start++;
        }
        int end = this.segments.size();
        while(end > 0 &&
                this.segments.get(end - 1) instanceof TextMessageSegment textSeg &&
                textSeg.getContent().isBlank()) {
            end--;
        }
        for(int i = start; i < end; i++) {
            newSegments.add(this.segments.get(i));
        }
        return new MessageSegmentList(newSegments);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        this.segments.forEach(seg -> {
            result.append(seg.toString());
        });
        return result.toString();
    }

    public boolean isSimpleText() {
        return this.segments.size() == 1 && this.segments.getFirst() instanceof TextMessageSegment;
    }

    public boolean equalsText(String text, boolean trim) {
        if(this.segments.size() == 1 &&
           this.segments.getFirst() instanceof TextMessageSegment segment) {
            return Objects.equals(text, trim ? segment.getContent().trim() : segment.getContent());
        }
        return false;
    }

    public boolean equalsText(String text) {
        return this.equalsText(text, true);
    }

    public boolean equalsAtWithText(long userId, String text, boolean trim) {
        if(this.segments.size() == 2 &&
            this.segments.getFirst() instanceof AtMessageSegment atSegment &&
            this.segments.get(1) instanceof TextMessageSegment textSegment) {
            return atSegment.getQQ() == userId &&
                    Objects.equals(text, trim ? textSegment.getContent().trim() : textSegment.getContent());
        }
        return false;
    }

    public boolean equalsAtWithText(long userId, String text) {
        return this.equalsAtWithText(userId, text, true);
    }

    public boolean equalsAtBotWithText(String text, boolean trim) {
        return this.equalsAtWithText(AccountInfo.getBotId(), text, trim);
    }

    public boolean equalsAtBotWithText(String text) {
        return this.equalsAtBotWithText(text, true);
    }
}
