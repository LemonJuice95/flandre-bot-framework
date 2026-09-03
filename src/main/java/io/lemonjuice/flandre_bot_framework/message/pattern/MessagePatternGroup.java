package io.lemonjuice.flandre_bot_framework.message.pattern;

import io.lemonjuice.flandre_bot_framework.message.pattern.node.MessagePatternNode;
import lombok.Getter;

import java.util.*;

@Getter
public class MessagePatternGroup {
    private final Set<MessagePatternNode> startNodes;
    private final int groupId;

    private MessagePatternGroup(Set<MessagePatternNode> startNodes, int groupId) {
        this.startNodes = startNodes;
        this.groupId = groupId;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Getter
    public static class Builder {
        private final Set<MessagePatternNode> prevNodes = new HashSet<>();
        private final Set<MessagePatternNode> firstNodes = new HashSet<>();
        private final Set<MessagePatternNode> startNodes = new HashSet<>();
        private int groupId;

        public Builder addPrevNode(MessagePatternNode node) {
            this.prevNodes.add(node);
            return this;
        }

        public Builder addPrevNodes(Collection<MessagePatternNode> prevNodes) {
            this.prevNodes.addAll(prevNodes);
            return this;
        }

        public Builder addFirstNode(MessagePatternNode node) {
            this.firstNodes.add(node);
            return this;
        }

        public Builder addFirstNodes(Collection<MessagePatternNode> nextNodes) {
            this.firstNodes.addAll(nextNodes);
            return this;
        }

        public Builder addStartNode(MessagePatternNode node) {
            this.startNodes.add(node);
            return this;
        }

        public Builder addStartNodes(Collection<MessagePatternNode> nextNodes) {
            this.startNodes.addAll(nextNodes);
            return this;
        }

        public Builder setGroupId(int groupId) {
            this.groupId = groupId;
            return this;
        }

        public MessagePatternGroup build() {
            return new MessagePatternGroup(Set.copyOf(this.startNodes), this.groupId);
        }
    }
}
