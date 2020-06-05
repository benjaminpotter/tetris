package io;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.Buffer;

public class Resources {

    /*** LoadImage ************************************
     * loads and returns a buffered image from a specified *
     * filepath                                            *
     ******************************************************/
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

    /*** SaveInt ************************************
     * save an integer to a specified file                *
     ******************************************************/
    public static void SaveInt (int save, String filepath) {
        try {
            FileWriter fw = new FileWriter(filepath);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(String.valueOf(save));
            bw.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /*** LoadInt ************************************
     * load an integer from a specified file                *
     ******************************************************/
    public static int LoadInt(String filepath) {
        int load = -1;
        try {
            FileReader fr = new FileReader(filepath);
            BufferedReader br = new BufferedReader(fr);
            load =  Integer.parseInt(br.readLine());
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return load;
    }
}
