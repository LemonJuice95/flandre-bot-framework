package test.command;

import io.lemonjuice.flandre_bot_framework.message.MessageSegmentList;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessagePattern;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.AtNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.RegexNode;
import io.lemonjuice.flandre_bot_framework.message.segment.AtMessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

public class MessagePatternTest {
    @Test
    public void test() {
        MessageSegmentList segments = new MessageSegmentList(List.of(
                new AtMessageSegment(123L),
                new TextMessageSegment(" 测试123")
                ));
        MessagePattern msgPattern = MessagePattern.builder()
                .nextNode(new AtNode(123L))
                .nextNode(new RegexNode(Pattern.compile("测试(\\d+)")))
                .build();
        boolean matches = msgPattern.matcher(segments).matches();
        if(matches) {
            System.out.println(".........................");
        } else {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    }
}
