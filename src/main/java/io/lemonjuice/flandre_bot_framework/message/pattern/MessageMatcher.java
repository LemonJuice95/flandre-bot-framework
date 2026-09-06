package io.lemonjuice.flandre_bot_framework.message.pattern;

import io.lemonjuice.flandre_bot_framework.message.MessageSegmentList;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.MessagePatternNode;
import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import io.lemonjuice.flandre_bot_framework.utils.data.Pair;

import java.util.*;

public class MessageMatcher {
    private final MessagePattern pattern;
    private MessageSegmentList segments;
    private final Queue<State> states;
    private final Set<State> visitedStates = new HashSet<>();
    private final Map<Integer, CaptureGroup> captureGroups = new HashMap();

    private Boolean matches;

    MessageMatcher(MessagePattern pattern, MessageSegmentList segments) {
        this.pattern = pattern;
        this.segments = segments;
        this.states = new PriorityQueue<>();

        State firstState = new State(0, this.pattern.getHeadNode());
        firstState.captureGroups = new HashMap<>();
        this.states.add(firstState);
        this.matches = null;
    }

    public void reset(MessageSegmentList newInput) {
        this.states.clear();
        this.matches = null;
        this.segments = newInput;

        State firstState = new State(0, this.pattern.getHeadNode());
        firstState.captureGroups = new HashMap<>();
        this.states.add(firstState);
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
                this.captureGroups.putAll(currentState.captureGroups);
                break;
            }
            if(currentState.nextSegIndex == this.segments.size()) {
                continue;
            }
            MessageSegment nextSegment = this.segments.get(currentState.nextSegIndex);
            if(nextSegment != null) {
                for (MessagePatternNode nextNode : currentState.currentNode.getNextNodes()) {
                    State nextState = new State(
                            currentState.nextSegIndex + 1,
                            nextNode,
                            currentState.priority - this.pattern.getEdgeValues().getOrDefault(new MessagePattern.Edge(currentState.currentNode, nextNode), 0)
                    );

                    if (!this.visitedStates.contains(nextState) && nextNode.validateCondition(nextSegment)) {

                        nextState.captureGroups = currentState.captureGroups != null ? new HashMap<>(currentState.captureGroups) : new HashMap<>();

                        for (Integer groupId : nextNode.getGroupIds()) {
                            nextState.captureGroups.compute(groupId, (k, v) -> {
                                if(v == null) return new CaptureGroup(currentState.nextSegIndex, currentState.nextSegIndex + 1);
                                return v.matchAt(currentState.nextSegIndex);
                            });
                        }

                        this.states.add(nextState);
                        this.visitedStates.add(nextState);
                    }
                }
            }
        }

        return this.matches;
    }

    public MessageSegmentList group(int groupId) {
        if(this.matches == null) {
            throw new IllegalStateException("在尚未进行匹配/无法匹配时获取捕获组");
        }
        CaptureGroup captureGroup = this.captureGroups.get(groupId);
        if(captureGroup == null) {
            throw new IllegalArgumentException("无效的组id");
        }
        return new MessageSegmentList(this.segments.subList(captureGroup.startIdx, captureGroup.endIdx));
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

    private static class State implements Comparable<State> {
        public final int nextSegIndex;
        public final MessagePatternNode currentNode;
        public final int priority;
        public Map<Integer, CaptureGroup> captureGroups;

        public State(int nextSegIndex, MessagePatternNode currentNode, int priority) {
            this.nextSegIndex = nextSegIndex;
            this.currentNode = currentNode;
            this.priority = priority;
        }

        public State(int nextSegIndex, MessagePatternNode currentNode) {
            this(nextSegIndex, currentNode, 0);
        }

        @Override
        public int compareTo(State o) {
            return Integer.compare(this.priority, o.priority);
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

    private static class CaptureGroup {
        public final int startIdx; //inclusive
        public final int endIdx; //exclusive

        public CaptureGroup() {
            this.startIdx = -1;
            this.endIdx = -1;
        }

        public CaptureGroup(int startIdx, int endIdx) {
            this.startIdx = startIdx;
            this.endIdx = endIdx;
        }

        public CaptureGroup matchAt(int index) {
            return new CaptureGroup(this.startIdx == -1 ? index : this.startIdx, index + 1);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) return true;
            if(obj instanceof CaptureGroup captureGroup) {
                return captureGroup.startIdx == this.startIdx && captureGroup.endIdx == this.endIdx;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.startIdx, this.endIdx);
        }
    }
}
