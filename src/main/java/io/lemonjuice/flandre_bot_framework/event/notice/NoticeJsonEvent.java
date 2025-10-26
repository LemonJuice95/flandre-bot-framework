package io.lemonjuice.flandre_bot_framework.event.notice;

import io.lemonjuice.flandre_bot_framework.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONObject;

/**
 * 推送时机: OneBot协议中"通知"类型事件的统一入口点<br>
 * 今后将考虑对通知进行拆分
 */
@AllArgsConstructor
@Getter
public class NoticeJsonEvent extends Event {
    private final JSONObject notice;
}
