package test;

import io.lemonjuice.flandre_bot_framework.network.WSClientCore;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class ContextManagerTest {
    @Test
    public void getGroupList() {
        JSONObject request = new JSONObject();
        request.put("action", "get_group_list");
        JSONObject response = WSClientCore.getInstance().request(request);
    }
}
