package io.lemonjuice.flandre_bot_framework.event.notice;

import io.lemonjuice.flandre_bot_framework.event.Event;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

@Log4j2
@Getter
public class NoticeEvent extends Event {
    protected final long time;
    protected final long selfId;
    protected final NoticeType type;
    protected final long userId;

    public NoticeEvent(long time, long selfId, NoticeType type, long userId) {
        this.time = time;
        this.selfId = selfId;
        this.type = type;
        this.userId = userId;
    }

    public NoticeEvent(JSONObject noticeJson) {
        this.time = noticeJson.optLong("time", -1L);
        this.selfId = noticeJson.optLong("self_id", -1L);
        this.type = NoticeType.forName(noticeJson.optString("notice_type", ""));
        if(this.type == NoticeType.UNKNOWN) {
            log.warn("未知的通知类型: {}", noticeJson.optString("notice_type", ""));
        }
        this.userId = noticeJson.optLong("user_id", -1L);
    }
}
