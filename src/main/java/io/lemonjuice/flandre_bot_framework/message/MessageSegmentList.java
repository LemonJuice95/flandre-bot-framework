package io.lemonjuice.flandre_bot_framework.message;

import io.lemonjuice.flandre_bot_framework.account.AccountInfo;
import io.lemonjuice.flandre_bot_framework.message.segment.AtMessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;
import lombok.Getter;

import java.util.*;

@Getter
public class MessageSegmentList implements List<MessageSegment> {
    private final List<MessageSegment> segments;

    public MessageSegmentList() {
        this.segments = new ArrayList<>();
    }

    public MessageSegmentList(List<MessageSegment> segments) {
        this.segments = segments;
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
        List<MessageSegment> segments = trim ? this.trim().getSegments() : this.getSegments();
        if(segments.size() == 2 &&
                segments.getFirst() instanceof AtMessageSegment atSegment &&
                segments.get(1) instanceof TextMessageSegment textSegment) {
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
    public int size() {
        return this.segments.size();
    }

    @Override
    public boolean isEmpty() {
        return this.segments.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.segments.contains(o);
    }

    @Override
    public Iterator<MessageSegment> iterator() {
        return this.segments.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.segments.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.segments.toArray(a);
    }

    @Override
    public boolean add(MessageSegment segment) {
        return this.segments.add(segment);
    }

    @Override
    public MessageSegment remove(int index) {
        return this.segments.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        return this.segments.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.segments.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends MessageSegment> c) {
        return this.segments.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends MessageSegment> c) {
        return this.segments.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.segments.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.segments.retainAll(c);
    }

    @Override
    public void clear() {
        this.segments.clear();
    }

    @Override
    public MessageSegment get(int index) {
        return this.segments.get(index);
    }

    @Override
    public MessageSegment set(int index, MessageSegment element) {
        return this.segments.set(index, element);
    }

    @Override
    public void add(int index, MessageSegment element) {
        this.segments.add(index, element);
    }

    @Override
    public int indexOf(Object o) {
        return this.segments.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.segments.lastIndexOf(o);
    }

    @Override
    public ListIterator<MessageSegment> listIterator() {
        return this.segments.listIterator();
    }

    @Override
    public ListIterator<MessageSegment> listIterator(int index) {
        return this.segments.listIterator(index);
    }

    @Override
    public List<MessageSegment> subList(int fromIndex, int toIndex) {
        return this.segments.subList(fromIndex, toIndex);
    }
}
