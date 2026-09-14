package io.lemonjuice.flandre_bot_framework.message.segment;

import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;

@Getter
public class FileMessageSegment extends MessageSegment {
    private final String file;
    private final String fileId;
    private final long fileSize;

    public FileMessageSegment(String fileName) {
        this(fileName, "", 0);
    }

    public FileMessageSegment(String fileName, String fileId, long fileSize) {
        super("file");
        this.file = fileName;
        this.fileId = fileId;
        this.fileSize = fileSize;
    }

    public FileMessageSegment(JSONObject msgData) {
        super("file");
        this.file = msgData.optString("file", "");
        this.fileId = msgData.optString("file_id", "");
        long fileSize;
        try {
            fileSize = Long.parseLong(msgData.getString("file_size"));
        } catch (JSONException | NumberFormatException e) {
            fileSize = -1;
        }
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return String.format("[文件:%s]", this.file);
    }

    @Override
    public JSONObject serializeMsgData() {
        JSONObject data = new JSONObject();
        data.put("file", this.file);
        return data;
    }
}
