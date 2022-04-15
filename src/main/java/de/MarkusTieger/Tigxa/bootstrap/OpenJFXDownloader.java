package de.MarkusTieger.Tigxa.bootstrap;

import de.MarkusTieger.Tigxa.http.HttpUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class OpenJFXDownloader {


    private static final String WIN_DOWNLOAD_x64 = "https://download2.gluonhq.com/openjfx/17.0.2/openjfx-17.0.2_windows-x64_bin-sdk.zip";

    private static final String LINUX_DOWNLOAD_x64 = "https://download2.gluonhq.com/openjfx/17.0.2/openjfx-17.0.2_linux-x64_bin-sdk.zip";
    private static final String LINUX_DOWNLOAD_AARCH64 = "https://download2.gluonhq.com/openjfx/17.0.2/openjfx-17.0.2_linux-aarch64_bin-sdk.zip";

    private static final String OSX_DOWNLOAD_x64 = "https://download2.gluonhq.com/openjfx/17.0.2/openjfx-17.0.2_osx-x64_bin-sdk.zip";
    private static final String OSX_DOWNLOAD_AARCH64 = "https://download2.gluonhq.com/openjfx/17.0.2/openjfx-17.0.2_osx-aarch64_bin-sdk.zip";


    public static String getDownload(){
        String os = System.getProperty("os.name", "Linux");
        String arch = System.getProperty("os.arch", "amd64");

        if(os.toLowerCase().contains("win".toLowerCase())){
            if(arch.equalsIgnoreCase("amd64")) return WIN_DOWNLOAD_x64;
        }
        if(os.toLowerCase().contains("Linux".toLowerCase())){
            if(arch.equalsIgnoreCase("amd64")){
                return LINUX_DOWNLOAD_x64;
            } else {
                return LINUX_DOWNLOAD_AARCH64;
            }
        }
        if(os.toLowerCase().contains("osx".toLowerCase())){
            if(arch.equalsIgnoreCase("amd64")){
                return OSX_DOWNLOAD_x64;
            } else {
                return OSX_DOWNLOAD_AARCH64;
            }
        }
        return null;
    }

    public static boolean isInstalled(){
        String home = System.getProperty("user.home", ".");
        File file = new File(home, "openjfx/libs");
        return file.exists();
    }

    public static void launch() throws Throwable {
        String home = System.getProperty("user.home", ".");
        File file = new File(home, "openjfx");

        File libs = new File(file, "libs");
        if(!libs.exists()) libs.mkdirs();

        String jvm = System.getProperty("java.home", "java");

        String suffix = System.setProperty("os.name", "Linux").toLowerCase().contains("win".toLowerCase()) ? ".exe" : "";

        if(!jvm.equalsIgnoreCase("java")){
            File hf = new File(jvm);
            jvm = new File(hf, "bin/java" + suffix).getAbsolutePath();
        }

        ProcessBuilder builder = new ProcessBuilder(jvm, "--module-path", libs.getAbsolutePath(), "--add-modules=javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web", "-jar", Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        builder.start();
    }

    public static void downloadAndRelaunch(String download) throws Throwable {
        String home = System.getProperty("user.home", ".");
        File file = new File(home, "openjfx");
        if(!file.exists()) file.mkdirs();

        File target = new File("openjfx.zip");
        if(!target.exists()) target.createNewFile();

        int len;
        byte[] buffer = new byte[1024];

        FileOutputStream out = new FileOutputStream(target);

        URL url = new URL(download);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", HttpUtils.AGENT);
        InputStream in = con.getInputStream();

        while((len = in.read(buffer)) > 0){
            out.write(buffer, 0, len);
        }
        out.flush();
        out.close();
        in.close();

        FileInputStream fis = new FileInputStream(target);
        ZipInputStream zis = new ZipInputStream(fis);
        ZipEntry ze = null;
        while((ze = zis.getNextEntry()) != null){
            File tar = new File(file, ze.getName());
            if(ze.isDirectory()){
                if(!tar.exists()) tar.mkdirs();
            } else {
                if(!tar.exists()) tar.createNewFile();
                out = new FileOutputStream(tar);
                while((len = zis.read(buffer)) > 0){
                    out.write(buffer, 0, len);
                }
                out.flush();
                out.close();
            }

            zis.closeEntry();
        }
        zis.close();

        launch();
    }
}
