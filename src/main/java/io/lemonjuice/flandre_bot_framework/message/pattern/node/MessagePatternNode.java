package io.lemonjuice.flandre_bot_framework.message.pattern.node;

import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessagePatternNode {
    private final Set<MessagePatternNode> nextNodes = new HashSet<>();
    private final Condition condition;
    @Getter
    private final List<Integer> groupIds = new ArrayList<>();

    public MessagePatternNode(Condition condition) {
        this.condition = condition;
    }

    public void addGroup(int groupId) {
        this.groupIds.add(groupId);
    }

    public void removeGroup(int groupId) {
        this.groupIds.remove(groupId);
    }

    public Set<MessagePatternNode> getNextNodes() {
        return new HashSet<>(nextNodes);
    }

    public void addNextNode(MessagePatternNode node) {
        this.nextNodes.add(node);
    }

    public boolean validateCondition(MessageSegment nextSegment) {
        return this.condition.validate(nextSegment);
    }

    public interface Condition {
        public boolean validate(MessageSegment nextSegment);
    }
}