package de.MarkusTieger.Tigxa.extension.impl;

import de.MarkusTieger.Tigxa.extension.IExtension;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Base64;

public abstract class BasicExtension implements IExtension {

    private static final String DEFAULT_PATH = "/res/gui/extensions/404.png";
    private final String name, version;
    private final String[] authors;

    private final BufferedImage icon, dark_icon;

    public BasicExtension(String name, String version, String[] authors, URL path) {
        this.name = name;
        this.version = version;
        this.authors = authors;

        BufferedImage img = null;
        try {
            img = ImageIO.read(path);
        } catch (Throwable e) {
            try {
                img = ImageIO.read(BasicExtension.class.getResource(DEFAULT_PATH));
            } catch (Throwable ex) {
            }
        }
        icon = img;

        BufferedImage i = null;
        if(img != null){
            i = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            i.getGraphics().drawImage(img, 0, 0, null);

            for (int x = 0; x < i.getWidth(); x++) {
                for (int y = 0; y < i.getHeight(); y++) {
                    int rgba = i.getRGB(x, y);
                    Color col = new Color(rgba, true);
                    col = new Color(255 - col.getRed(),
                            255 - col.getGreen(),
                            255 - col.getBlue(),
                            col.getAlpha());
                    i.setRGB(x, y, col.getRGB());
                }
            }
        }
        dark_icon = i;
    }

    public BasicExtension(String name, String version, String[] authors, String base64) {
        this.name = name;
        this.version = version;
        this.authors = authors;

        BufferedImage img = null;
        try {
            byte[] b = Base64.getDecoder().decode(base64);
            ByteArrayInputStream in = new ByteArrayInputStream(b);
            img = ImageIO.read(in);
            in.close();
        } catch (Throwable e) {
            try {
                img = ImageIO.read(BasicExtension.class.getResource(DEFAULT_PATH));
            } catch (Throwable ex) {
            }
        }
        icon = img;

        BufferedImage i = null;
        if(img != null){
            i = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            i.getGraphics().drawImage(img, 0, 0, null);

            for (int x = 0; x < i.getWidth(); x++) {
                for (int y = 0; y < i.getHeight(); y++) {
                    int rgba = i.getRGB(x, y);
                    Color col = new Color(rgba, true);
                    col = new Color(255 - col.getRed(),
                            255 - col.getGreen(),
                            255 - col.getBlue());
                    i.setRGB(x, y, col.getRGB());
                }
            }
        }
        dark_icon = i;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getAuthors() {
        return authors;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public Image getIcon() {
        return icon;
    }

    @Override
    public Image getDarkIcon() {
        return dark_icon;
    }
}
