package de.MarkusTieger.Tigxa.update;

import de.MarkusTieger.Tigxa.Bootstrap;
import de.MarkusTieger.Tigxa.Browser;
import de.MarkusTieger.Tigxa.http.HttpUtils;
import lombok.Getter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class Updater {

    @Getter
    private boolean updated = false;

    public Version getLatestVersion(){
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("https://github.com/TigerSystems/tigxa/releases/latest/download/version.txt").openConnection();
            con.setRequestProperty("User-Agent", HttpUtils.AGENT);
            InputStream in = con.getInputStream();
            byte[] data = in.readAllBytes();
            in.close();

            Scanner x = new Scanner(new ByteArrayInputStream(data));

            if(!x.hasNextLine()) return null;
            String ver = x.nextLine();

            if(!x.hasNextLine()) return null;
            String build = x.nextLine();

            if(!x.hasNextLine()) return null;
            String commit = x.nextLine();

            return new Version(ver, build, commit);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkJar() {
        URL url1 = Bootstrap.class.getProtectionDomain().getCodeSource().getLocation();
        URL url2 = Browser.class.getProtectionDomain().getCodeSource().getLocation();
        return url1.sameFile(url2);
    }

    public String getOS(){
        String testlib = "jfxmedia";

        if(Bootstrap.class.getResource("/libjfxmedia.so") != null) return "linux";
        if(Bootstrap.class.getResource("/jfxmedia.dll") != null) return "win";
        if(Bootstrap.class.getResource("/libjfxmedia.dylib") != null) return "mac";

        return "linux";
    }

    public void update(Version version, Consumer<Double> percend){
        if(!checkJar()) throw new RuntimeException("This Build of the " + Browser.FULL_NAME + " can't update.");

        URL resource = Bootstrap.class.getProtectionDomain().getCodeSource().getLocation();
        if(resource == null) throw new RuntimeException("This Build of the " + Browser.FULL_NAME + " can't update.");
        if(!resource.getProtocol().equalsIgnoreCase("file")) throw new RuntimeException("This Build of the " + Browser.FULL_NAME + " can't update.");

        String os = getOS();
        String path = "https://github.com/TigerSystems/tigxa/releases/download/" + version.version() + "-" + version.build() + "/" + Browser.NAME.toLowerCase() + "-" + version.version() + "-" + os + "-all.jar";
        System.out.println("PATH: " + path);
        try {

            File target = new File(resource.toURI());

            URL url = new URL(path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", HttpUtils.AGENT);
            long length = con.getContentLength();

            int len;
            byte[] buffer = new byte[1024];

            InputStream in = con.getInputStream();

            if(!target.exists()) target.createNewFile();
            FileOutputStream out = new FileOutputStream(target);
            double readed = 0D;
            while((len = in.read(buffer)) > 0){
                out.write(buffer, 0, len);
                readed += len;
                percend.accept(((readed * 100D) / ((double) length)));
            }
            out.flush();
            out.close();
            in.close();

            percend.accept(-1D);
            updated = true;
            System.out.println("FINISHED!");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
