package io.lemonjuice.flandre_bot_framework.event.notice;

import io.lemonjuice.flandre_bot_framework.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONObject;

@AllArgsConstructor
@Getter
public class NoticeJsonEvent extends Event {
    private final JSONObject notice;
}
