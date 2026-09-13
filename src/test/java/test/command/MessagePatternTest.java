package test.command;

import io.lemonjuice.flandre_bot_framework.message.MessageSegmentList;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessageMatcher;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessagePattern;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.AnySegmentNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.AtNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.RegexNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.TypedSegmentNode;
import io.lemonjuice.flandre_bot_framework.message.segment.AtMessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.ImageMessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class MessagePatternTest {
    @Test
    public void test() {
        MessageSegmentList segments = new MessageSegmentList(List.of(
                new AtMessageSegment(123L),
                new TextMessageSegment(" 测试123"),
                new TextMessageSegment(" 测试456"),
                new TextMessageSegment(" 测试789 "),
                new ImageMessageSegment("图片123"),
                new ImageMessageSegment("图片456"),
                new ImageMessageSegment("图片789"),
                new AtMessageSegment(456L)
                ));
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
}
