package io.lemonjuice.flandre_bot_framework.utils;

import lombok.extern.log4j.Log4j2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

@Log4j2
public class CQCode {
    public static String reply(long msgId) {
        return String.format("[CQ:reply,id=%d]", msgId);
    }

    public static String at(long uid) {
        return String.format("[CQ:at,qq=%d]", uid);
    }

    public static String image(String url) {
        return String.format("[CQ:image,file=%s]", url);
    }

    public static String image(File file) {
        return String.format("[CQ:image,file=file:///%s]", file.getAbsolutePath());
    }

    public static String image(BufferedImage image) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", stream);
            String base64 = Base64.getEncoder().encodeToString(stream.toByteArray());
            return String.format("[CQ:image,file=base64://%s]", base64);
        } catch (IOException e) {
            log.error("图片转base64失败！", e);
            return "";
        }
    }
}
