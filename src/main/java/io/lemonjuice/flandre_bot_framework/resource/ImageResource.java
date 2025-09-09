package io.lemonjuice.flandre_bot_framework.resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ImageResource extends Resource<BufferedImage> {
    public ImageResource(String resPath) {
        super(resPath, new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB));
    }

    @Override
    protected BufferedImage load(InputStream input) throws IOException {
        return ImageIO.read(input);
    }
}
