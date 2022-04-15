package de.MarkusTieger.Tigxa.bootstrap;

import de.MarkusTieger.Tigxa.Browser;
import de.MarkusTieger.Tigxa.update.Updater;
import de.MarkusTieger.Tigxa.update.Version;

import javax.swing.*;

public class ErrorLauncher {

    public static void launch(LaunchError state){
        if(state == LaunchError.FX_NOT_LOADED) launchFXLoader();
        if(state == LaunchError.FX_NOT_FOUND) launchFXError();
        if(state == LaunchError.SWT_NOT_LOADED) launchSWTError();
        if(state == LaunchError.SWING_UNKNOWN_ERROR) launchSwingError();
        if(state == LaunchError.ALL_FAILED) launchRecovery();
    }

    private static void launchFXLoader() {
        String download = OpenJFXDownloader.getDownload();
        if(download == null){
            launch(LaunchError.FX_NOT_FOUND);
            return;
        }
        int option = JOptionPane.showConfirmDialog(null, "OpenJFX is not installed. Would you like to install it?", "OpenJFX not installed!", JOptionPane.YES_NO_OPTION);
        while (option == JOptionPane.CLOSED_OPTION){
            option = JOptionPane.showConfirmDialog(null, "OpenJFX is not installed. Would you like to install it?", "OpenJFX not installed!", JOptionPane.YES_NO_OPTION);
        }
        if(option == JOptionPane.YES_OPTION){
            try {
                OpenJFXDownloader.downloadAndRelaunch(download);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else launch(LaunchError.FX_NOT_FOUND);
    }

    private static void launchRecovery() {
        int option = JOptionPane.showConfirmDialog(null, "All-Failed! Would you like to start the Updater?", "All-Failed", JOptionPane.YES_NO_OPTION);
        while (option == JOptionPane.CLOSED_OPTION){
            option = JOptionPane.showConfirmDialog(null, "All-Failed! Would you like to start the Updater?", "All-Failed", JOptionPane.YES_NO_OPTION);
        }

        if(option == JOptionPane.YES_OPTION){
            try {
                Updater updater = new Updater();
                Version latest = updater.getLatestVersion();
                updater.update(latest, System.out::println);
            } catch(Throwable e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Updater Failed! " + e, "Updater Failed!", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            System.exit(0);
        }
    }

    private static void launchSwingError() {
        int option = JOptionPane.showConfirmDialog(null, "Failed to Launch Tigxa using Swing.\nWould you like to start the Launcher in None-Mode?", "Swing Error", JOptionPane.YES_NO_OPTION);
        while (option == JOptionPane.CLOSED_OPTION){
            option = JOptionPane.showConfirmDialog(null, "Failed to Launch Tigxa using Swing.\nWould you like to start the Launcher in None-Mode?", "Swing Error", JOptionPane.YES_NO_OPTION);
        }

        if(option == JOptionPane.YES_OPTION){
            try {
                Browser.start(Browser.Mode.SWING);
            } catch(Throwable e){
                e.printStackTrace();
                launch(LaunchError.ALL_FAILED);
            }
        } else {
            System.exit(0);
        }
    }

    private static void launchSWTError(){
        int option = JOptionPane.showConfirmDialog(null, "Failed to Launch Tigxa using DJ-Natives-SWT.\nWould you like to start the Launcher in Swing-Mode?", "DJ-Natives-SWT Error", JOptionPane.YES_NO_OPTION);
        while (option == JOptionPane.CLOSED_OPTION){
            option = JOptionPane.showConfirmDialog(null, "Failed to Launch Tigxa using DJ-Natives-SWT.\nWould you like to start the Launcher in Swing-Mode?", "DJ-Natives-SWT Error", JOptionPane.YES_NO_OPTION);
        }

        if(option == JOptionPane.YES_OPTION){
            try {
                Browser.start(Browser.Mode.SWING);
            } catch(Throwable e){
                e.printStackTrace();
                launch(LaunchError.SWING_UNKNOWN_ERROR);
            }
        } else {
            System.exit(0);
        }
    }

    private static void launchFXError() {
        int option = JOptionPane.showConfirmDialog(null, "Failed to Launch Tigxa using JavaFX.\nWould you like to start the Launcher in DJ-Natives-SWT-Mode?", "JavaFX Error", JOptionPane.YES_NO_OPTION);
        while (option == JOptionPane.CLOSED_OPTION){
            option = JOptionPane.showConfirmDialog(null, "Failed to Launch Tigxa using JavaFX.\nWould you like to start the Launcher in DJ-Natives-SWT-Mode?", "JavaFX Error", JOptionPane.YES_NO_OPTION);
        }

        if(option == JOptionPane.YES_OPTION){
            try {
                Browser.start(Browser.Mode.SWT);
            } catch(Throwable e){
                e.printStackTrace();
                launch(LaunchError.SWT_NOT_LOADED);
            }
        } else {
            System.exit(0);
        }
    }

}
