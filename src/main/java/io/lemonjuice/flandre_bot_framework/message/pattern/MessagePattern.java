package io.lemonjuice.flandre_bot_framework.message.pattern;

import io.lemonjuice.flandre_bot_framework.message.MessageSegmentList;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.AnySegmentNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.MessagePatternNode;
import io.lemonjuice.flandre_bot_framework.model.Message;
import lombok.Getter;

import java.util.*;

@Getter
public class MessagePattern {
    private final MessagePatternNode headNode;
    private final Set<MessagePatternNode> finalNodes;
    private final List<MessagePatternGroup> groups;
    private final Map<Edge, Integer> edgeValues;

    private MessagePattern(MessagePatternNode headNode, Set<MessagePatternNode> finalNodes, List<MessagePatternGroup> groups, Map<Edge, Integer> edgeValues) {
        this.headNode = headNode;
        this.finalNodes = finalNodes;
        this.groups = groups;
        this.edgeValues = edgeValues;
    }

    public MessageMatcher matcher(MessageSegmentList segments) {
        return new MessageMatcher(this, segments);
    }

    public MessageMatcher matcher(Message message) {
        return this.matcher(message.message);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final MessagePatternNode headNode;
        private final Set<MessagePatternNode> optNodes;
        private final List<MessagePatternNode> allNodes;
        private final List<MessagePatternNode> currentNodes;

        private int nextGroupId = 1;
        private final Deque<MessagePatternGroup.Builder> groupStack;
        private final List<MessagePatternGroup> groups = new ArrayList<>();

        private final Map<Edge, Integer> edgeValues = new HashMap<>();

        public Builder() {
            this.headNode = new AnySegmentNode();
            this.groupStack = new ArrayDeque<>();
            this.allNodes = new ArrayList<>();
            this.optNodes = new HashSet<>();
            this.currentNodes = new ArrayList<>();
            this.currentNodes.add(this.headNode);
        }

        public Builder startGroup() {
            this.groupStack.add(
                    MessagePatternGroup.builder()
                            .addPrevNodes(this.currentNodes)
                            .setGroupId(this.nextGroupId)
            );
            this.nextGroupId++;
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

            MessagePatternGroup.Builder builder = this.groupStack.pollLast();

            if(builder != null) {
                //处理startNodes
                Set<MessagePatternNode> startNodes = new HashSet<>();
                Set<MessagePatternNode> nextNodes = new HashSet<>(builder.getFirstNodes());
                Set<MessagePatternNode> nextNextNodes = new HashSet<>();
                Set<MessagePatternNode> visited = new HashSet<>();

                while(!nextNodes.isEmpty()) {
                    startNodes.addAll(nextNodes);

                    for(MessagePatternNode nnode : nextNodes) {
                        if(this.optNodes.contains(nnode)) {
                            nextNextNodes.addAll(nnode.getNextNodes());
                        }
                    }

                    nextNodes.clear();
                    Set<MessagePatternNode> tempSet = nextNodes;
                    nextNodes = nextNextNodes;
                    nextNextNodes = tempSet;
                }
                builder.addStartNodes(startNodes);

                //处理循环
                if(loopFlag) {
                    Set<MessagePatternNode> targetNodes = new HashSet<>(builder.getStartNodes());

                    while(!targetNodes.isEmpty()) {
                        for(MessagePatternNode tnode : targetNodes) {
                            visited.add(tnode);
                            this.currentNodes.forEach(cnode -> {
                                cnode.addNextNode(tnode);
                                this.edgeValues.compute(new Edge(cnode, tnode),
                                        (k, v) -> {
                                    int value = 2 + builder.getGroupId() - tnode.getGroupIds().getFirst() + (this.optNodes.contains(tnode) ? 1 : 0);
                                    if(v == null) return value;
                                    return Math.max(v, value);
                                });
                            });
                            if(this.optNodes.contains(tnode)) {
                                nextNodes.addAll(tnode.getNextNodes().stream().filter(n -> !visited.contains(n)).toList());
                            }
                        }
                        targetNodes.clear();
                        Set<MessagePatternNode> tempSet = targetNodes;
                        targetNodes = nextNodes;
                        nextNodes = tempSet;
                    }
                }

                //处理可选
                if(optFlag) {
                    this.currentNodes.addAll(builder.getPrevNodes());
                    builder.getPrevNodes().forEach(pnode -> {
                        builder.getStartNodes().forEach(snode -> {
                            this.edgeValues.compute(new Edge(pnode, snode), (k, v) -> {
                                if(v == null) return 1;
                                return v+1;
                            });
                        });
                    });
                }

                this.groups.add(builder.getGroupId(), builder.build());
            }
            return this;
        }

        public Builder nextNode(MessagePatternNode node) {
            this.currentNodes.forEach(cnode -> cnode.addNextNode(node));
            this.currentNodes.clear();
            this.currentNodes.add(node);
            this.allNodes.add(node);
            if(!this.groupStack.isEmpty()) {
                this.groupStack.forEach(b -> node.addGroup(b.getGroupId()));
            }
            this.handleGroupFirstNodes();
            return this;
        }

        public Builder nextOptNode(MessagePatternNode node) {
            this.currentNodes.forEach(cnode -> {
                cnode.addNextNode(node);
                //新node必然不存在已有边，无需使用compute
                this.edgeValues.putIfAbsent(new Edge(cnode, node), 1);
            });
            this.currentNodes.add(node);
            this.allNodes.add(node);
            this.optNodes.add(node);
            if(!this.groupStack.isEmpty()) {
                this.groupStack.forEach(b -> node.addGroup(b.getGroupId()));
            }
            this.handleGroupFirstNodes();
            return this;
        }

        public Builder nextOrNodes(MessagePatternNode... nodes) {
            this.currentNodes.forEach(cnode -> Arrays.stream(nodes).forEach(cnode::addNextNode));
            this.currentNodes.clear();
            this.currentNodes.addAll(Arrays.asList(nodes));
            this.allNodes.addAll(Arrays.asList(nodes));
            if(!this.groupStack.isEmpty()) {
                for(MessagePatternNode node : nodes) {
                    this.groupStack.forEach(b -> node.addGroup(b.getGroupId()));
                }
            }
            this.handleGroupFirstNodes();
            return this;
        }

        public Builder nextOptOrNodes(MessagePatternNode... nodes) {
            this.currentNodes.forEach(cnode -> {
                Arrays.stream(nodes).forEach(tnode -> {
                    cnode.addNextNode(tnode);
                    this.edgeValues.putIfAbsent(new Edge(cnode, tnode), 1);
                });
            });
            this.currentNodes.addAll(Arrays.asList(nodes));
            this.optNodes.addAll(Arrays.asList(nodes));
            this.allNodes.addAll(Arrays.asList(nodes));
            if(!this.groupStack.isEmpty()) {
                for(MessagePatternNode node : nodes) {
                    this.groupStack.forEach(b -> node.addGroup(b.getGroupId()));
                }
            }
            this.handleGroupFirstNodes();
            return this;
        }

        public Builder nextLoopNode(MessagePatternNode node) {
            node.addNextNode(node);
            this.currentNodes.forEach(cnode -> cnode.addNextNode(node));
            this.currentNodes.clear();
            this.currentNodes.add(node);
            this.allNodes.add(node);
            if(!this.groupStack.isEmpty()) {
                this.groupStack.forEach(b -> node.addGroup(b.getGroupId()));
            }
            this.edgeValues.put(new Edge(node, node),
                    2 + node.getGroupIds().getLast() - node.getGroupIds().getFirst() + 2);
            this.handleGroupFirstNodes();
            return this;
        }

        public Builder nextOptLoopNode(MessagePatternNode node) {
            node.addNextNode(node);
            this.currentNodes.forEach(cnode -> {
                cnode.addNextNode(node);
                this.edgeValues.putIfAbsent(new Edge(cnode, node), 1);
            });
            this.currentNodes.add(node);
            this.optNodes.add(node);
            this.allNodes.add(node);
            if(!this.groupStack.isEmpty()) {
                this.groupStack.forEach(b -> node.addGroup(b.getGroupId()));
            }
            this.edgeValues.put(new Edge(node, node),
                    2 + node.getGroupIds().getLast() - node.getGroupIds().getFirst() + 2);
            this.handleGroupFirstNodes();
            return this;
        }

        private void handleGroupFirstNodes() {
            if (this.groupStack.peekLast() != null && this.groupStack.peekLast().getFirstNodes().isEmpty()) {
                this.groupStack.peekLast().addFirstNodes(this.currentNodes);
            }
        }

        public MessagePattern build() {
            while(!this.groupStack.isEmpty()) {
                this.endGroup();
            }
            return new MessagePattern(
                    this.headNode,
                    Set.copyOf(this.currentNodes),
                    this.groups,
                    this.edgeValues
            );
        }
    }

    @Getter
    public static class Edge {
        private final MessagePatternNode startNode;
        private final MessagePatternNode toNode;

        public Edge(MessagePatternNode startNode, MessagePatternNode toNode) {
            this.startNode = startNode;
            this.toNode = toNode;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null) return false;
            if(obj == this) return true;
            if(obj instanceof Edge edge) {
                return this.startNode == edge.startNode && this.toNode == edge.toNode;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.startNode, this.toNode);
        }
    }

    public enum GroupFlag {
        LOOP,
        OPTIONAL
    }
}
