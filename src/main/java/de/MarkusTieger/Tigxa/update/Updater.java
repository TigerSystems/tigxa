package de.MarkusTieger.Tigxa.update;

import de.MarkusTieger.Tigxa.Browser;
import de.MarkusTieger.Tigxa.http.HttpUtils;
import lombok.Getter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class Updater {

    private static final Logger LOGGER = LogManager.getLogger(Updater.class);

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
        URL url1 = Updater.class.getProtectionDomain().getCodeSource().getLocation();
        URL url2 = Browser.class.getProtectionDomain().getCodeSource().getLocation();
        return url1.sameFile(url2);
    }

    public String getOS(){
        String testlib = "jfxmedia";

        if(Browser.class.getResource("/lib" + testlib + ".so") != null) return "linux";
        if(Browser.class.getResource("/" + testlib + ".dll") != null) return "win";
        if(Browser.class.getResource("/lib" + testlib + ".dylib") != null) return "mac";

        return "linux";
    }

    public String getARCH(){
        String arch = System.getProperty("os.arch", "amd64");
        if(arch.equalsIgnoreCase("amd64")){
            return arch;
        } else return "aarch64";
    }

    public boolean isDebugBuild(){
        return Browser.VERSION.equalsIgnoreCase("0.0.0") && Browser.BUILD.equalsIgnoreCase("-") && Browser.COMMIT_HASH.equalsIgnoreCase("-");
    }

    public void update(Version version, Consumer<Double> percend){
        if(isDebugBuild()) throw new RuntimeException("You can't update a Debug-Build!");
        if(!checkJar()) throw new RuntimeException("This Build of the " + Browser.FULL_NAME + " can't update.");

        URL resource = Browser.class.getProtectionDomain().getCodeSource().getLocation();
        if(resource == null) throw new RuntimeException("This Build of the " + Browser.FULL_NAME + " can't update.");
        if(!resource.getProtocol().equalsIgnoreCase("file")) throw new RuntimeException("This Build of the " + Browser.FULL_NAME + " can't update.");

        String os = getOS();
        String arch = getARCH();

        String path = "https://github.com/TigerSystems/tigxa/releases/download/" + version.version() + "-" + version.build() + "/" + Browser.NAME.toLowerCase() + "-" + version.version() + "-" + os + "-" + arch + "-all.jar";
        try {

            File target = new File(resource.toURI());

            LOGGER.debug("Opening Streams...");

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

                double p = ((readed * 100D) / ((double) length));
                percend.accept(p);

                LOGGER.debug("Percend: " + p);
            }

            LOGGER.debug("Closing Streams...");

            out.flush();
            out.close();
            in.close();

            percend.accept(-1D);
            updated = true;
            LOGGER.info("Update Finished!");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

	public void updateSafly(Version version, Consumer<Double> percend){
        if(isDebugBuild()) throw new RuntimeException("You can't update a Debug-Build!");
        if(!checkJar()) throw new RuntimeException("This Build of the " + Browser.FULL_NAME + " can't update.");

        URL resource = Browser.class.getProtectionDomain().getCodeSource().getLocation();
        if(resource == null) throw new RuntimeException("This Build of the " + Browser.FULL_NAME + " can't update.");
        if(!resource.getProtocol().equalsIgnoreCase("file")) throw new RuntimeException("This Build of the " + Browser.FULL_NAME + " can't update.");

        String os = getOS();
        String arch = getARCH();

        String path = "https://github.com/TigerSystems/tigxa/releases/download/" + version.version() + "-" + version.build() + "/" + Browser.NAME.toLowerCase() + "-" + version.version() + "-" + os + "-" + arch + "-all.jar";
        try {

            File target = new File(resource.toURI());

            LOGGER.debug("Opening Streams...");

            URL url = new URL(path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", HttpUtils.AGENT);
            long length = con.getContentLength();

            int len;
            byte[] buffer = new byte[1024];

            InputStream in = con.getInputStream();

            if(!target.exists()) target.createNewFile();
            
            OutputStream out = new ByteArrayOutputStream();
            
            double readed = 0D;
            while((len = in.read(buffer)) > 0){
                out.write(buffer, 0, len);
                readed += len;

                double p = ((readed * 100D) / ((double) length));
                percend.accept(p);

                LOGGER.debug("Percend: " + p);
            }

            byte[] data = ((ByteArrayOutputStream)out).toByteArray();
            
            out = new FileOutputStream(target);
            out.write(data);
            
            LOGGER.debug("Closing Streams...");

            out.flush();
            out.close();
            in.close();

            percend.accept(-1D);
            updated = true;
            LOGGER.info("Update Finished!");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
