package de.MarkusTieger.Tigxa.bootstrap;

import de.MarkusTieger.Tigxa.Browser;
import de.MarkusTieger.Tigxa.update.Updater;
import de.MarkusTieger.Tigxa.update.Version;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;

public class ErrorLauncher {

    private static final Logger LOGGER = LogManager.getLogger(ErrorLauncher.class);

    public static void launch(LaunchError state){
        if(state == LaunchError.FX_NOT_LOADED) launchFXLoader();
        if(state == LaunchError.FX_NOT_FOUND) launchFXError();
        if(state == LaunchError.SWT_NOT_LOADED) launchSWTError();
        if(state == LaunchError.SWING_UNKNOWN_ERROR) launchSwingError();
        if(state == LaunchError.ALL_FAILED) launchRecovery();
    }

    private static void launchFXLoader() {

        LOGGER.info("Checking OpenJFX-Installation...");

        if(OpenJFXDownloader.isInstalled()){
            try {
                LOGGER.info("Installed! Launching...");
                OpenJFXDownloader.launch();
                return;
            } catch (Throwable e) {
                LOGGER.warn("Launch Failed. Installing again...", e);
            }
        }

        LOGGER.info("Try find OpenJFX Download...");
        String download = OpenJFXDownloader.getDownload();
        if(download == null){
            LOGGER.warn("OpenJFX Download Failed. No Download Found.");
            launch(LaunchError.FX_NOT_FOUND);
            return;
        }
        LOGGER.info("Requesting Install...");
        int option = JOptionPane.showConfirmDialog(null, "OpenJFX is not installed. Would you like to install it?", "OpenJFX not installed!", JOptionPane.YES_NO_OPTION);
        while (option == JOptionPane.CLOSED_OPTION){
            option = JOptionPane.showConfirmDialog(null, "OpenJFX is not installed. Would you like to install it?", "OpenJFX not installed!", JOptionPane.YES_NO_OPTION);
        }
        if(option == JOptionPane.YES_OPTION){
            LOGGER.info("Request Accepted. Installing and Relaunch...");
            try {
                OpenJFXDownloader.downloadAndRelaunch(download);
            } catch (Throwable e) {
                LOGGER.warn("Downloading Failed!", e);
            }
        } else launch(LaunchError.FX_NOT_FOUND);
    }

    private static void launchRecovery() {
        LOGGER.info("Requesting Updater...");
        int option = JOptionPane.showConfirmDialog(null, "All-Failed! Would you like to start the Updater?", "All-Failed", JOptionPane.YES_NO_OPTION);
        while (option == JOptionPane.CLOSED_OPTION){
            option = JOptionPane.showConfirmDialog(null, "All-Failed! Would you like to start the Updater?", "All-Failed", JOptionPane.YES_NO_OPTION);
        }

        if(option == JOptionPane.YES_OPTION){
            LOGGER.info("Starting Updater...");
            try {
                startUpdater();
                LOGGER.info("Update-Finished!");
            } catch(Throwable e){
                LOGGER.warn("Updater Failed!", e);
                JOptionPane.showMessageDialog(null, "Updater Failed! " + e, "Updater Failed!", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            LOGGER.info("Stopping Bootstrap...");
            System.exit(0);
        }
    }

    public static void startUpdater() throws Throwable {
        Updater updater = new Updater();
        Version latest = updater.getLatestVersion();
        updater.update(latest, (p) -> {});
    }

    private static void launchSwingError() {
        LOGGER.info("Requesting None-Mode...");
        int option = JOptionPane.showConfirmDialog(null, "Failed to Launch Tigxa using Swing.\nWould you like to start the Launcher in None-Mode?", "Swing Error", JOptionPane.YES_NO_OPTION);
        while (option == JOptionPane.CLOSED_OPTION){
            option = JOptionPane.showConfirmDialog(null, "Failed to Launch Tigxa using Swing.\nWould you like to start the Launcher in None-Mode?", "Swing Error", JOptionPane.YES_NO_OPTION);
        }

        if(option == JOptionPane.YES_OPTION){
            LOGGER.info("Launching using Engine \"" + Browser.Mode.NONE.name() + "\"...");
            try {
                Browser.start(Browser.Mode.NONE);
            } catch(Throwable e){
                LOGGER.warn("Launching Failed. Starting Recovery...", e);
                launch(LaunchError.ALL_FAILED);
            }
        } else {
            LOGGER.info("Stopping Bootstrap...");
            System.exit(0);
        }
    }

    private static void launchSWTError(){
        LOGGER.info("Requesting Swing-Mode...");
        int option = JOptionPane.showConfirmDialog(null, "Failed to Launch Tigxa using DJ-Natives-SWT.\nWould you like to start the Launcher in Swing-Mode?", "DJ-Natives-SWT Error", JOptionPane.YES_NO_OPTION);
        while (option == JOptionPane.CLOSED_OPTION){
            option = JOptionPane.showConfirmDialog(null, "Failed to Launch Tigxa using DJ-Natives-SWT.\nWould you like to start the Launcher in Swing-Mode?", "DJ-Natives-SWT Error", JOptionPane.YES_NO_OPTION);
        }

        if(option == JOptionPane.YES_OPTION){
            LOGGER.info("Launching using Engine \"" + Browser.Mode.SWING.name() + "\"...");
            try {
                Browser.start(Browser.Mode.SWING);
            } catch(Throwable e){
                LOGGER.warn("Launching Failed.", e);
                launch(LaunchError.SWING_UNKNOWN_ERROR);
            }
        } else {
            LOGGER.info("Stopping Bootstrap...");
            System.exit(0);
        }
    }

    private static void launchFXError() {
        LOGGER.info("Requesting DJ-Natives-SWT-Mode...");
        int option = JOptionPane.showConfirmDialog(null, "Failed to Launch Tigxa using JavaFX.\nWould you like to start the Launcher in DJ-Natives-SWT-Mode?", "JavaFX Error", JOptionPane.YES_NO_OPTION);
        while (option == JOptionPane.CLOSED_OPTION){
            option = JOptionPane.showConfirmDialog(null, "Failed to Launch Tigxa using JavaFX.\nWould you like to start the Launcher in DJ-Natives-SWT-Mode?", "JavaFX Error", JOptionPane.YES_NO_OPTION);
        }

        if(option == JOptionPane.YES_OPTION){
            LOGGER.info("Launching using Engine \"" + Browser.Mode.DJ_NATIVE_SWT.name() + "\"...");
            try {
                Browser.start(Browser.Mode.DJ_NATIVE_SWT);
            } catch(Throwable e){
                LOGGER.warn("Launching Failed.", e);
                launch(LaunchError.SWT_NOT_LOADED);
            }
        } else {
            LOGGER.info("Stopping Bootstrap...");
            System.exit(0);
        }
    }

}
