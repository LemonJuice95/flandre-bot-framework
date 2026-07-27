package io.lemonjuice.flandre_bot_framework.message.pattern;

import io.lemonjuice.flandre_bot_framework.message.MessageSegmentList;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.MessagePatternNode;
import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import io.lemonjuice.flandre_bot_framework.utils.data.Pair;

import java.util.*;

public class MessageMatcher {
    private final MessagePattern pattern;
    private final MessageSegmentList segments;
    private final Queue<State> states;
    private final Set<State> visitedStates = new HashSet<>();

    private Boolean matches;

    MessageMatcher(MessagePattern pattern, MessageSegmentList segments) {
        this.pattern = pattern;
        this.segments = segments;
        this.states = new ArrayDeque<>();
        this.states.add(new State(0, this.pattern.getHeadNode()));
        this.matches = null;
    }

    public void reset(MessageSegmentList newInput) {
        this.states.clear();
        this.matches = null;
        this.states.add(new State(0, this.pattern.getHeadNode()));
    }

    public boolean matches() {
        if(this.matches != null) {
            return this.matches;
        }

        this.matches = Boolean.FALSE;

        while(!this.states.isEmpty()) {
            State currentState = this.states.poll();
            if(this.pattern.getFinalNodes().contains(currentState.currentNode) && currentState.nextSegIndex == this.segments.size()) {
                this.matches = Boolean.TRUE;
                break;
            }
            if(currentState.nextSegIndex == this.segments.size()) {
                continue;
            }
            MessageSegment nextSegment = this.segments.get(currentState.nextSegIndex);
            if(nextSegment != null) {
                for (MessagePatternNode nextNode : currentState.currentNode.getNextNodes()) {
                    State nextState = new State(currentState.nextSegIndex + 1, nextNode);
                    if (!this.visitedStates.contains(nextState) && nextNode.validateCondition(nextSegment)) {
                        this.states.add(nextState);
                        this.visitedStates.add(nextState);
                    }
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
            for (MessagePatternNode currentNode : this.states.peek().getSecond().getNextNodes()) {
                if (currentNode.validateCondition(firstSeg)) {
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
                for (MessagePatternNode currentNode : this.pattern.getHeadNode().getNextNodes()) {
                    if (currentNode.validateCondition(firstSegment)) {
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
            for(MessagePatternNode currentNode : this.pattern.getHeadNode().getNextNodes()) {
                if(currentNode.validateCondition(currentState.getFirst().peekFirst())) {
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

    private static class State {
        public final int nextSegIndex;
        public final MessagePatternNode currentNode;

        public State(int nextSegIndex, MessagePatternNode currentNode) {
            this.nextSegIndex = nextSegIndex;
            this.currentNode = currentNode;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) return true;
            if(obj instanceof State state) {
                return this.currentNode == state.currentNode && this.nextSegIndex == state.nextSegIndex;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.nextSegIndex, this.currentNode);
        }
    }
}
