package io.lemonjuice.flandre_bot_framework.message.pattern;

import io.lemonjuice.flandre_bot_framework.message.MessageSegmentList;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.AnySegmentNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.MessagePatternNode;
import io.lemonjuice.flandre_bot_framework.utils.data.Pair;
import lombok.Getter;

import java.util.*;

@Getter
public class MessagePattern {
    private final MessagePatternNode headNode;
    private final Set<MessagePatternNode> finalNodes;

    private MessagePattern(MessagePatternNode headNode, Set<MessagePatternNode> finalNodes) {
        this.headNode = headNode;
        this.finalNodes = finalNodes;
    }

    public MessageMatcher matcher(MessageSegmentList segments) {
        return new MessageMatcher(this, segments);
    }

    public Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final MessagePatternNode headNode;
        private final Deque<Pair<List<MessagePatternNode>, List<MessagePatternNode>>> groupStack;
        private final Set<MessagePatternNode> optNodes;
        private final List<MessagePatternNode> allNodes;
        private final List<MessagePatternNode> currentNodes;

        public Builder() {
            this.headNode = new AnySegmentNode();
            this.groupStack = new ArrayDeque<>();
            this.allNodes = new ArrayList<>();
            this.optNodes = new HashSet<>();
            this.currentNodes = new ArrayList<>();
            this.currentNodes.add(this.headNode);
        }

        public Builder startGroup() {
            this.groupStack.add(Pair.of(new ArrayList<>(this.currentNodes), new ArrayList<>()));
            return this;
        }

        public Builder endGroup(GroupFlag... flags) {
            if(this.groupStack.isEmpty()) {
                throw new IllegalStateException("没有可供闭合的组起始点");
            }

            boolean loopFlag = false;
            boolean optFlag = false;

            for(GroupFlag flag : flags) {
                if(flag == GroupFlag.LOOP) {
                    loopFlag = true;
                }
                if(flag == GroupFlag.OPTIONAL) {
                    optFlag = true;
                }
            }

            Pair<List<MessagePatternNode>, List<MessagePatternNode>> pair = this.groupStack.pollLast();
            if(pair != null) {
                if(loopFlag) {
                    Set<MessagePatternNode> targetNodes = new HashSet<>(pair.getSecond());
                    Set<MessagePatternNode> nextNodes = new HashSet<>();
                    while(!targetNodes.isEmpty()) {
                        for(MessagePatternNode tnode : targetNodes) {
                            this.currentNodes.forEach(cnode -> cnode.addNextNode(tnode));
                            if(this.optNodes.contains(tnode)) {
                                nextNodes.addAll(tnode.getNextNodes());
                            }
                        }
                        targetNodes.clear();
                        Set<MessagePatternNode> tempSet = targetNodes;
                        targetNodes = nextNodes;
                        nextNodes = tempSet;
                    }
                }
                if(optFlag) {
                    this.currentNodes.addAll(pair.getFirst());
                }
            }
            return this;
        }

        public Builder nextNode(MessagePatternNode node) {
            this.currentNodes.forEach(cnode -> cnode.addNextNode(node));
            this.currentNodes.clear();
            this.currentNodes.add(node);
            this.allNodes.add(node);
            if (this.groupStack.peekLast() != null && this.groupStack.peekLast().getSecond().isEmpty()) {
                this.groupStack.peekLast().getSecond().addAll(this.currentNodes);
            }
            return this;
        }

        public Builder nextOptNode(MessagePatternNode node) {
            this.currentNodes.forEach(cnode -> cnode.addNextNode(node));
            this.currentNodes.add(node);
            this.allNodes.add(node);
            this.optNodes.add(node);
            if (this.groupStack.peekLast() != null && this.groupStack.peekLast().getSecond().isEmpty()) {
                this.groupStack.peekLast().getSecond().addAll(this.currentNodes);
            }
            return this;
        }

        public Builder nextOrNodes(MessagePatternNode... nodes) {
            this.currentNodes.forEach(cnode -> Arrays.stream(nodes).forEach(cnode::addNextNode));
            this.currentNodes.clear();
            this.currentNodes.addAll(Arrays.asList(nodes));
            this.allNodes.addAll(Arrays.asList(nodes));
            if (this.groupStack.peekLast() != null && this.groupStack.peekLast().getSecond().isEmpty()) {
                this.groupStack.peekLast().getSecond().addAll(this.currentNodes);
            }
            return this;
        }

        public Builder nextOptOrNodes(MessagePatternNode... nodes) {
            this.currentNodes.forEach(cnode -> Arrays.stream(nodes).forEach(cnode::addNextNode));
            this.currentNodes.addAll(Arrays.asList(nodes));
            this.optNodes.addAll(Arrays.asList(nodes));
            this.allNodes.addAll(Arrays.asList(nodes));
            if (this.groupStack.peekLast() != null && this.groupStack.peekLast().getSecond().isEmpty()) {
                this.groupStack.peekLast().getSecond().addAll(this.currentNodes);
            }
            return this;
        }

        public Builder nextLoopNode(MessagePatternNode node) {
            node.addNextNode(node);
            this.currentNodes.forEach(cnode -> cnode.addNextNode(node));
            this.currentNodes.clear();
            this.currentNodes.add(node);
            this.allNodes.add(node);
            if(this.groupStack.peekLast() != null && this.groupStack.peekLast().getSecond().isEmpty()) {
                this.groupStack.peekLast().getSecond().addAll(this.currentNodes);
            }
            return this;
        }

        public Builder nextOptLoopNode(MessagePatternNode node) {
            node.addNextNode(node);
            this.currentNodes.forEach(cnode -> cnode.addNextNode(node));
            this.currentNodes.add(node);
            this.optNodes.add(node);
            this.allNodes.add(node);
            if(this.groupStack.peekLast() != null && this.groupStack.peekLast().getSecond().isEmpty()) {
                this.groupStack.peekLast().getSecond().addAll(this.currentNodes);
            }
            return this;
        }

        public MessagePattern build() {
            return new MessagePattern(this.headNode, new HashSet<>(this.currentNodes));
        }
    }

    public enum GroupFlag {
        LOOP,
        OPTIONAL
    }
}
