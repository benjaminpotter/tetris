package io;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Resources {

    public static BufferedImage LoadImage(String filepath) {
        BufferedImage img;

        try {
            img = ImageIO.read(new File(filepath));
            return img;
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
