package de.MarkusTieger.Tigxa.streaming.spotify;

import java.io.*;

public class Spotify {

    public static boolean ENABLE = false;

    public static File DIR = null;

    private static boolean started = false;

    public static void start(){
        if(started) return;
        started = true;

        File jar = new File(DIR, "server.jar");
        if(!jar.exists()) {
            try {
                jar.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(jar.length() != 37864404L) {
            try {
                InputStream in = Spotify.class.getResourceAsStream("/res/jars/lovspotify-1.6.1.jar.res");
                if(in == null) throw new IOException("JAR not found!");
                FileOutputStream out = new FileOutputStream(jar);
                int len;
                byte[] buffer = new byte[1024];
                while((len = in.read(buffer)) > 0){
                    out.write(buffer, 0, len);
                }
                out.flush();
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            String jvm = System.getProperty("java.home", "java");

            String suffix = System.setProperty("os.name", "Linux").toLowerCase().contains("win".toLowerCase()) ? ".exe" : "";

            if(!jvm.equalsIgnoreCase("java")){
                File hf = new File(jvm);
                jvm = new File(hf, "bin/java" + suffix).getAbsolutePath();
            }

            ProcessBuilder builder = new ProcessBuilder(jvm, "-jar", jar.getAbsolutePath());
            builder.directory(DIR);
            Process p = builder.start();
            Runtime.getRuntime().addShutdownHook(new Thread(p::destroyForcibly, "Spotify-Destroyer"));
        } catch (Throwable e){
            e.printStackTrace();
        }
    }

}
