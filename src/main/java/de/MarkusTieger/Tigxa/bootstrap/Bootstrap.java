package de.MarkusTieger.Tigxa.bootstrap;

import de.MarkusTieger.Tigxa.Browser;
import org.apache.log4j.*;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Method;

public class Bootstrap {

    private static final Logger LOGGER = Logger.getLogger(Bootstrap.class);

    public static void main(String[] args) {

        if(args.length > 0){
            if(args[0].equalsIgnoreCase("recovery")){
                ErrorLauncher.launch(LaunchError.ALL_FAILED);
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

}
