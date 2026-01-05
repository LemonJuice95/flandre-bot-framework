package io.lemonjuice.flandre_bot_framework.message.segment;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

@Getter
@Setter
@Log4j2
public class ImageMessageSegment extends MessageSegment {
    private final String file;
    private final boolean flashImage;
    private final String url;

    private boolean useCache;
    private boolean useProxy;
    private int timeout;

    public ImageMessageSegment(String file, boolean flashImage, String url) {
        super("image");
        this.file = file;
        this.flashImage = flashImage;
        this.url = url;
        this.useCache = true;
        this.useProxy = true;
        this.timeout = -1;
    }

    public ImageMessageSegment(String file, boolean flashImage) {
        this(file, flashImage, "");
    }

    public ImageMessageSegment(String file) {
        this(file, false, "");
    }

    public ImageMessageSegment(File file, boolean flashImage) {
        this(String.format("file:///%s", file.getAbsolutePath()), flashImage);
    }

    public ImageMessageSegment(File file) {
        this(file, false);
    }

    public ImageMessageSegment(BufferedImage image, String formatName, boolean flashImage) {
        super("image");
        String file = "";
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            ImageIO.write(image, formatName, stream);
            String base64 = Base64.getEncoder().encodeToString(stream.toByteArray());
            file = String.format("base64://%s", base64);
        } catch (IOException e) {
            log.error("图片转base64失败！", e);
        }
        this.file = file;
        this.flashImage = flashImage;
        this.url = "";
        this.useCache = true;
        this.useProxy = true;
        this.timeout = -1;
    }

    public ImageMessageSegment(BufferedImage image, String formatName) {
        this(image, formatName, false);
    }

    public ImageMessageSegment(JSONObject data) {
        this(data.optString("file", ""), data.has("type"), data.optString("url", ""));
    }

    @Override
    public String toString() {
        return String.format("[%s:%s]", this.flashImage ? "闪照" : "图片", this.file);
    }

    @Override
    public JSONObject serializeMsgData() {
        JSONObject data = new JSONObject();
        data.put("file", this.file);
        if(this.flashImage) {
            data.put("type", "flash");
        }
        data.put("cache", this.useCache ? "1" : "0");
        data.put("proxy", this.useProxy ? "1" : "0");
        if(this.timeout != -1) {
            data.put("timeout", String.valueOf(this.timeout));
        }
        return data;
    }
}
