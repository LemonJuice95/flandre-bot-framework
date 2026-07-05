package io.lemonjuice.flandre_bot_framework.event.notice;

import lombok.Getter;
import org.json.JSONObject;

@Getter
public class GroupUploadFileEvent extends GroupNoticeEvent {
    protected final String fileId;
    protected final String fileName;
    protected final long fileSize;
    protected final long fileBusId;

    public GroupUploadFileEvent(
            long time, long selfId, NoticeType type, long userId,
            long groupId, String fileId, String fileName, long fileSize, long fileBusId) {
        super(time, selfId, type, userId, groupId);
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileBusId = fileBusId;
    }

    public GroupUploadFileEvent(JSONObject noticeJson) {
        super(noticeJson);
        JSONObject fileJson = noticeJson.optJSONObject("file", new JSONObject());
        this.fileId = fileJson.optString("id", "");
        this.fileName = fileJson.optString("name", "");
        this.fileSize = fileJson.optLong("size", -1L);
        this.fileBusId = fileJson.optLong("busid", -1L);
    }
}
