package io.lemonjuice.flandre_bot_framework.network;

import org.json.JSONObject;

import javax.annotation.Nullable;

public interface INetworkImpl {
    public void sendMsg(String action, @Nullable JSONObject msg);
    public JSONObject request(String action, @Nullable JSONObject msg);
    public boolean connect(String url, String token);
    public void close();
}
