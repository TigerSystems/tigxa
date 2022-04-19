package org.chromium.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class Resource {
	
	public static BufferedImage getResouceImage(String path) {
		BufferedImage img = null;
		try {
		    img = ImageIO.read(getResourceURL(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}

	public static URL getResourceURL(String path){
		return Resource.class.getResource("/" + path);
	}
	
}
