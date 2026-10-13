package test.command;

import io.lemonjuice.flandre_bot_framework.message.MessageSegmentList;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessageMatcher;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessagePattern;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.AnySegmentNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.AtNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.TypedSegmentNode;
import io.lemonjuice.flandre_bot_framework.message.segment.AtMessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.ImageMessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

public class MessagePatternTest {
    private static final MessageSegmentList segments = new MessageSegmentList(List.of(
            new AtMessageSegment(123L),
            new TextMessageSegment(" 测试123"),
            new TextMessageSegment(" 测试456"),
            new TextMessageSegment(" 测试789 "),
            new ImageMessageSegment("图片123"),
            new ImageMessageSegment("图片456"),
            new ImageMessageSegment("图片789"),
            new AtMessageSegment(456L)
    ));

    @Test
    public void testFind() {
        MessagePattern msgPattern = MessagePattern.builder()
                .startGroup()
                .nextNode(new TypedSegmentNode(TextMessageSegment.class))
                .endGroup(MessagePattern.GroupFlag.LOOP, MessagePattern.GroupFlag.OPTIONAL)
                .startGroup()
                .nextLoopNode(new TypedSegmentNode(ImageMessageSegment.class))
                .endGroup(MessagePattern.GroupFlag.LOOP, MessagePattern.GroupFlag.OPTIONAL)
                .build();
        MessageMatcher matcher = msgPattern.matcher(segments);
        while (matcher.find()) {
            System.out.println(Objects.toString(matcher.group(1)));
            System.out.println(Objects.toString(matcher.group(2)));
            System.out.println("------");
        }
    }

    @Test
    public void testGreedyMatches() {
        MessagePattern msgPattern = MessagePattern.builder()
                .nextNode(new AtNode(123L))
                .startGroup()
                .nextLoopNode(new TypedSegmentNode(TextMessageSegment.class))
                .endGroup()
                .startGroup()
                .nextOrNodes(new TypedSegmentNode(TextMessageSegment.class), new TypedSegmentNode(ImageMessageSegment.class))
                .endGroup(MessagePattern.GroupFlag.LOOP)
                .nextLoopNode(new AnySegmentNode())
                .build();
        MessageMatcher matcher = msgPattern.matcher(segments);
        Assertions.assertTrue(matcher.matches());
        System.out.println(Objects.toString(matcher.group(1)));
        System.out.println(Objects.toString(matcher.group(2)));
    }
}
