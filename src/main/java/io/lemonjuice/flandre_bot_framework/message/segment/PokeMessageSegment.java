package io.lemonjuice.flandre_bot_framework.message.segment;

import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 戳一戳消息段
 * （注：对应电脑端窗口抖动，请勿与双击头像的“戳一戳”动作混淆）<br>
 * 类型与id列表参见<a href="https://github.com/mamoe/mirai/blob/f5eefae7ecee84d18a66afce3f89b89fe1584b78/mirai-core/src/commonMain/kotlin/net.mamoe.mirai/message/data/HummerMessage.kt#L49">Mirai的PokeMessage类</a>
 */
@Getter
public class PokeMessageSegment extends MessageSegment {
    private final int type;
    private final int id;

    public PokeMessageSegment(int type, int id) {
        super("poke");
        this.type = type;
        this.id = id;
    }

    public PokeMessageSegment(JSONObject msgData) {
        super("poke");
        int type;
        int id;
        try {
            type = Integer.parseInt(msgData.getString("type"));
        } catch (JSONException | NumberFormatException e) {
            type = -1;
        }
        try {
            id = Integer.parseInt(msgData.getString("id"));
        } catch (JSONException | NumberFormatException e) {
            id = -1;
        }
        this.type = type;
        this.id = id;
    }

    @Override
    public String toString() {
        return "[戳一戳]";
    }

    @Override
    public JSONObject serializeMsgData() {
        JSONObject data = new JSONObject();
        data.put("type", String.valueOf(this.type));
        data.put("id", String.valueOf(this.id));
        return data;
    }
}
