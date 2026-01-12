package io.lemonjuice.flandre_bot_framework.event.meta;

import io.lemonjuice.flandre_bot_framework.event.Event;
import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import org.json.JSONObject;

import java.util.Map;
import java.util.function.Function;

/**
 * 为开发者提供自定义消息段类型的接口
 * 以防止可能的特定OneBot实现中某种消息段类型缺失
 */
public class SegmentTypeRegisterEvent extends Event {
    private final Map<String, Function<JSONObject, MessageSegment>> segmentMap;

    public SegmentTypeRegisterEvent(Map<String, Function<JSONObject, MessageSegment>> segmentMap) {
        this.segmentMap = segmentMap;
    }

    public void register(String type, Function<JSONObject, MessageSegment> parser) {
        this.segmentMap.put(type, parser);
    }
}
