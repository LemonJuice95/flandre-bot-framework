package io.lemonjuice.flandre_bot_framework.utils;

import io.lemonjuice.flandre_bot_framework.message.segment.ImageMessageSegment;
import io.lemonjuice.flandre_bot_framework.network.NetworkContainer;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * OneBot中部分文件相关操作的入口
 * 目前仅支持图片获取，将在未来扩展
 */
public class FileHelper {
    /**
     * 获取图片数据
     * @param imgSeg 图片消息段
     * @return 图片数据
     * @throws IOException 无法从文件数据流读取图片时
     * @throws IllegalArgumentException 图片文件不存在时
     */
    public BufferedImage getImage(ImageMessageSegment imgSeg) throws IOException {
        return this.getImage(imgSeg.getFile());
    }

    /**
     * 获取图片数据
     * @param fileName OneBot提供的图片文件名
     * @return 图片数据
     * @throws IOException 无法从文件数据流读取图片时
     * @throws IllegalArgumentException 图片文件不存在时
     */
    public BufferedImage getImage(String fileName) throws IOException {
        File file = this.getImageFile(fileName);
        if(!file.exists()) {
            throw new IllegalArgumentException("图片文件不存在");
        }
        try (InputStream input = new FileInputStream(file)) {
            return ImageIO.read(input);
        }
    }

    /**
     * 获取图片文件
     * @param imgSeg 图片消息段
     * @return 图片文件
     */
    public File getImageFile(ImageMessageSegment imgSeg) {
        return this.getImageFile(imgSeg.getFile());
    }

    /**
     * 获取图片文件
     * @param fileName OneBot提供的图片文件名
     * @return 图片文件
     */
    public File getImageFile(String fileName) {

        return new File(this.getImagePath(fileName));
    }

    /**
     * 获取图片文件的路径文本
     * @param imgSeg 图片消息段
     * @return 图片路径文本
     */
    public String getImagePath(ImageMessageSegment imgSeg) {
        return this.getImagePath(imgSeg.getFile());
    }

    /**
     * 获取图片文件的路径文本
     * @param fileName OneBot提供的图片文件名
     * @return 图片路径文本
     */
    public String getImagePath(String fileName) {
        JSONObject response = this.getImageRequest(fileName);
        return response.getString("file");
    }

    private JSONObject getImageRequest(String fileName) {
        JSONObject data = new JSONObject();
        data.put("file", fileName);
        return NetworkContainer.getImpl().request("get_image", data).getJSONObject("data");
    }
}
