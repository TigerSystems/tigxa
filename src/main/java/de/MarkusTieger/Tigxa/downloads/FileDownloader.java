package de.MarkusTieger.Tigxa.downloads;

import de.MarkusTieger.Tigxa.http.HttpUtils;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class FileDownloader {

    @Getter
    @Setter
    private DownloadAction action = DownloadAction.CHOOSE;

    @Getter
    @Setter
    private File downloadDir;

    public void download(String str){
        try {
            URL url = new URL(str);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", HttpUtils.AGENT);

            for(Map.Entry<String, List<String>> e : CookieManager.getDefault().get(url.toURI(), con.getRequestProperties()).entrySet()){
                for(String value : e.getValue()){
                    con.addRequestProperty(e.getKey(), value);
                }
            }


            InputStream in = con.getInputStream();
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(downloadDir);
            chooser.showSaveDialog(null);
            if(chooser.getSelectedFile() != null){
                new Thread(() -> {

                    try {
                        File file = chooser.getSelectedFile();
                        if(!file.exists()) file.createNewFile();
                        FileOutputStream out = new FileOutputStream(file);
                        int len;
                        byte[] buffer = new byte[1024];
                        while((len = in.read(buffer)) > 0){
                            out.write(buffer, 0, len);
                        }
                        out.flush();
                        out.close();
                        in.close();
                    } catch (Throwable e){
                    }


                }, "File-Downloader").start();
            } else {
                in.close();
            }
        } catch (Throwable e) {
        }
    }

    public static enum DownloadAction {

        CHOOSE, SAVE_DEFAULT, RUN, SAVE_AS;

    }

}
