package io.lemonjuice.flandre_bot_framework.account;

import io.lemonjuice.flandre_bot_framework.network.NetworkContainer;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.json.JSONException;
import org.json.JSONObject;

@Log4j2
public class AccountInfo {
    @Getter
    private static volatile long botId = -1;
    @Getter
    private static volatile String botName = "";

    public static synchronized void init() {
        botId = -1;
        botName = "";

        JSONObject response = NetworkContainer.getImpl().request("get_login_info", null);
        if(response.optInt("retcode", -1) == 0) {
            JSONObject data = response.optJSONObject("data", new JSONObject());
            try {
                botId = data.getInt("user_id");
                botName = data.getString("nickname");
            } catch (JSONException e) {
                log.error("拉取当前登录账号信息失败！", e);
            }
        } else {
            log.error("拉取当前登录账号信息失败！");
        }
    }
}
