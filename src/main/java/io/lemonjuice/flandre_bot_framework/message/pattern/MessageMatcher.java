package io.lemonjuice.flandre_bot_framework.message.pattern;

import io.lemonjuice.flandre_bot_framework.message.MessageSegmentList;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.MessagePatternNode;
import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import io.lemonjuice.flandre_bot_framework.utils.data.Pair;

import java.util.*;

public class MessageMatcher {
    private final MessagePattern pattern;
    private final Queue<Pair<Deque<MessageSegment>, MessagePatternNode>> states;
    private Boolean matches;

    MessageMatcher(MessagePattern pattern, MessageSegmentList segments) {
        this.pattern = pattern;
        this.states = new ArrayDeque<>();
        this.states.add(Pair.of(new ArrayDeque<>(segments.getSegments()), this.pattern.getHeadNode()));
        this.matches = null;
    }

    public void reset(MessageSegmentList newInput) {
        this.states.clear();
        this.matches = null;
        this.states.add(Pair.of(new ArrayDeque<>(newInput.getSegments()), this.pattern.getHeadNode()));
    }

    public boolean matches() {
        if(this.matches != null) {
            return this.matches;
        }

        this.matches = Boolean.FALSE;

        while(!this.states.isEmpty()) {
            Pair<Deque<MessageSegment>, MessagePatternNode> currentState = this.states.poll();
            if(this.pattern.getFinalNodes().contains(currentState.getSecond()) && currentState.getFirst().isEmpty()) {
                this.matches = Boolean.TRUE;
                break;
            }
            Deque<MessageSegment> segments = new ArrayDeque<>(currentState.getFirst());
            if(segments.isEmpty()) {
                continue;
            }
            MessageSegment nextSegment = segments.peekFirst();
            for(MessagePatternNode nextNode : currentState.getSecond().getNextNodes()) {
                if(nextNode.validateCondition(nextSegment)) {
                    segments.pollFirst();
                    this.states.add(Pair.of(segments, nextNode));
                    segments = new ArrayDeque<>(currentState.getFirst());
                }
            }
        }

        return this.matches;
    }

    /* WIP
    public MessageSegmentList find() {
        if (this.matches == null && !this.states.isEmpty()) {
            boolean matchedNextNode = false;

            MessageSegment firstSeg = this.states.peek().getFirst().peekFirst();
            for (MessagePatternNode nextNode : this.states.peek().getSecond().getNextNodes()) {
                if (nextNode.validateCondition(firstSeg)) {
                    matchedNextNode = true;
                    break;
                }
            }

            if (!matchedNextNode) {
                this.matches = Boolean.FALSE;
            }
        }

        boolean found = false;
        List<MessageSegment> tempSegments;
        MessageSegment endSegment;
        findHead: {
            while (!this.states.isEmpty() && !this.states.peek().getFirst().isEmpty()) {
                MessageSegment firstSegment = this.states.peek().getFirst().peekFirst();
                for (MessagePatternNode nextNode : this.pattern.getHeadNode().getNextNodes()) {
                    if (nextNode.validateCondition(firstSegment)) {
                        tempSegments = new ArrayList<>(this.states.peek().getFirst());
                        break findHead;
                    }
                }
                this.states.peek().getFirst().pollFirst();
            }
        }
        while (!this.states.isEmpty()) {
            Pair<Deque<MessageSegment>, MessagePatternNode> currentState = this.states.peek();
            if (this.pattern.getFinalNodes().contains(currentState.getSecond())) {
                found = true;
                endSegment = currentState.getFirst().peek();
                this.states.clear();
                this.states.add(Pair.of(currentState.getFirst(), this.pattern.getHeadNode()));
                break;
            }
            for(MessagePatternNode nextNode : this.pattern.getHeadNode().getNextNodes()) {
                if(nextNode.validateCondition(currentState.getFirst().peekFirst())) {
                    this.states.add(Pair.of())
                }
            }
        }

        if (!found) {
            if (this.matches == null) {
                this.matches = Boolean.FALSE;
            }
            return null;
        }

        List<MessageSegment> result = new ArrayList<>();

    }*/
}
