package net.rktlvr.maze;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Texture {
    BufferedImage img;
    int w, h;

    Texture(String path) throws IOException {
        try {
            InputStream is = Texture.class.getClassLoader().getResourceAsStream(path);
            if (is == null) {
                throw new RuntimeException("Texture not found: " + path);
            }

            img = ImageIO.read(is);
            w = img.getWidth();
            h = img.getHeight();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    int sample(double x, double y) {
        int tx = (int)(x * w) & (w - 1);
        int ty = (int)(y * h) & (h - 1);
        return img.getRGB(tx, ty);
    }
}
