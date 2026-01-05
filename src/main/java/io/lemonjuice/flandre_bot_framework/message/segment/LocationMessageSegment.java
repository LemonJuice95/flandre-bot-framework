package io.lemonjuice.flandre_bot_framework.message.segment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 位置分享消息段
 */
public class LocationMessageSegment extends MessageSegment {
    private final double latitude;
    private final double longitude;
    private final String title;
    private final String content;

    public LocationMessageSegment(double latitude, double longitude, String title, String content) {
        super("location");
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.content = content;
    }

    public LocationMessageSegment(JSONObject msgData) {
        super("location");
        double latitude;
        double longitude;
        try {
            latitude = Double.parseDouble(msgData.getString("lat"));
        } catch (JSONException | NumberFormatException e) {
            latitude = -1;
        }
        try {
            longitude = Double.parseDouble(msgData.getString("lon"));
        } catch (JSONException | NumberFormatException e) {
            longitude = -1;
        }
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = msgData.optString("title", "");
        this.content = msgData.optString("content", "");
    }

    @Override
    public String toString() {
        return String.format("[位置分享%s]", this.title.isEmpty() ? "" : ":" + this.title);
    }

    @Override
    public JSONObject serializeMsgData() {
        JSONObject data = new JSONObject();
        data.put("lat", this.latitude);
        data.put("lon", this.longitude);
        if(!this.title.isEmpty()) {
            data.put("title", this.title);
        }
        if(!this.content.isEmpty()) {
            data.put("content", this.content);
        }
        return data;
    }
}
