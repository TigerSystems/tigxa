package de.markustieger.tigxa.gui.image;

import de.markustieger.tigxa.http.HttpUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoader {

    public static BufferedImage loadHTTPImage(URL url) {
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", HttpUtils.AGENT);
            con.setInstanceFollowRedirects(true);
            InputStream in = con.getInputStream();

            if (con.getHeaderField("location") != null) {
                in.close();
                URL redirect = new URL(con.getHeaderField("location"));
                con = (HttpURLConnection) redirect.openConnection();
                con.setRequestProperty("User-Agent", HttpUtils.AGENT);
                in = con.getInputStream();
            }

            BufferedImage image = ImageIO.read(in);
            in.close();
            return image;
        } catch (IOException e) {
            // e.printStackTrace();
        }
        return null;
    }

    public static ImageIcon loadHTTPImageAsIcon(URL url) {
        BufferedImage image = loadHTTPImage(url);
        if (image != null) return new ImageIcon(image);
        return null;
    }

    public static BufferedImage loadInternalImage(String path) {
        try {
            URL url = ImageLoader.class.getResource(path);
            if (url == null) return null;
            BufferedImage image = ImageIO.read(url);
            return image;
        } catch (IOException e) {
            // e.printStackTrace();
        }
        return null;
    }

    public static ImageIcon loadInternalImageAsIcon(String path) {
        BufferedImage image = loadInternalImage(path);
        if (image == null) return null;
        return new ImageIcon(image);
    }

}
