package io.lemonjuice.flandre_bot.utils;

public class CQCode {
    public static String reply(long msgId) {
        return "[CQ:reply,id=" + msgId + "]";
    }

    public static String at(long uid) {
        return "[CQ:at,qq=" + uid + "]";
    }

    public static String image(String url) {
        return "[CQ:image,file=" + url + "]";
    }
}
