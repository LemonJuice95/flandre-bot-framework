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
    private boolean found;
    private int startAt = -1;

    MessageMatcher(MessagePattern pattern, MessageSegmentList segments) {
        this.pattern = pattern;
        this.segments = segments;
        this.states = new PriorityQueue<>();
        this.matches = null;
        this.found = false;
    }

    /**
     * 用新的消息段列表重置整个匹配器
     * @param newInput 新的消息段列表
     */
    public void reset(MessageSegmentList newInput) {
        this.states.clear();
        this.visitedStates.clear();
        this.captureGroups.clear();
        this.matches = null;
        this.segments = newInput;
        this.found = false;
    }

    /**
     * 使用当前输入重置整个匹配器
     */
    public void reset() {
        this.reset(this.segments);
    }

    /**
     * 基于整个消息段列表进行一次匹配
     * @return 消息段列表是否匹配 {@link io.lemonjuice.flandre_bot_framework.message.pattern.MessagePattern}
     */
    public boolean matches() {
        if(this.matches != null) {
            return this.matches;
        }

        this.states.clear();
        State firstState = new State(0, this.pattern.getHeadNode());
        firstState.captureGroups = new HashMap<>();
        this.states.add(firstState);
        this.visitedStates.clear();
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

    /**
     * 基于整个消息段列表进行一次匹配，
     * 仅以相对可能较快的速度判断是否匹配，不进行贪婪匹配及组的捕获，
     * <b>在调用此方法后调用 {@link MessageMatcher#group(int)} 方法将导致异常</b>
     * @return 消息段列表是否匹配 {@link io.lemonjuice.flandre_bot_framework.message.pattern.MessagePattern}
     */
    public boolean simplyMatches() {
        if(this.matches != null) {
            return this.matches;
        }

        this.states.clear();
        State firstState = new State(0, this.pattern.getHeadNode());
        firstState.captureGroups = new HashMap<>();
        this.states.add(firstState);
        this.visitedStates.clear();
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

    /**
     * 在消息段列表及其子列表中进行匹配
     * @return 是否存在可匹配的子列表
     */
    public boolean find() {
        this.matches = null;
        this.captureGroups.clear();
        this.visitedStates.clear();
        this.found = false;

        List<State> matchedStates = new ArrayList<>();
        while(this.startAt < this.segments.size() && !this.found) {
            this.startAt++;

            this.states.clear();
            State firstState = new State(0, this.pattern.getHeadNode());
            firstState.captureGroups = new HashMap<>();
            this.states.add(firstState);

            while(!this.states.isEmpty()) {
                State currentState = this.states.poll();

                if(this.pattern.getFinalNodes().contains(currentState.currentNode)) {
                    matchedStates.add(currentState);

                    /* 当找到一个终态时，如果存在匹配结果更长的终态（即当前终态并非最长匹配）
                    那么更长的终态在现有贪婪匹配的情况下对于目前寻找到的终态而言必然为可达的（由被构建出的MessagePattern中的图具有一定线性特征决定）
                    此时扩展现有的其他状态将失去意义，故此清空现有状态以优化方法性能
                     */
                    this.states.clear();
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

            if(!matchedStates.isEmpty()) {
                this.found = true;
                State longestState = matchedStates.stream().max(Comparator.comparingInt(s -> s.nextSegIndex)).get();
                this.captureGroups.putAll(longestState.captureGroups);
                break;
            }
        }
        return this.found;
    }

    /**
     * 获取对应id捕获组捕获的内容
     * @param groupId 捕获组id（由第一个{@link MessagePattern.Builder#startGroup()}开始的组id为1，后续每个{@link MessagePattern.Builder#startGroup()}对应的组id依次+1，特别地，当传入的groupId为0时，返回整个匹配到的消息段列表
     * @return 对应组捕获到的消息段列表
     */
    public MessageSegmentList group(int groupId) {
        if(this.matches == null && !this.found) {
            throw new IllegalStateException("在尚未进行匹配/无法匹配时获取捕获组");
        }
        CaptureGroup captureGroup = this.captureGroups.get(groupId);
        if(captureGroup == null) {
            throw new IllegalArgumentException("无效的组id");
        }
        return new MessageSegmentList(this.segments.subList(captureGroup.startIdx, captureGroup.endIdx));
    }

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
