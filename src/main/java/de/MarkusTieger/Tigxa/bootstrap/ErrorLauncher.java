package de.MarkusTieger.Tigxa.bootstrap;

import de.MarkusTieger.Tigxa.Browser;

import javax.swing.*;

public class ErrorLauncher {

    public static void launch(FXError state){
        int option = JOptionPane.showConfirmDialog(null, "Failed to Launch Tigxa using JavaFX.\nWould you like to start the Launcher in Swing-Mode?", "JavaFX Error", JOptionPane.YES_NO_OPTION);
        while (option == JOptionPane.CLOSED_OPTION){
            option = JOptionPane.showConfirmDialog(null, "Failed to Launch Tigxa using JavaFX.\nWould you like to start the Launcher in Swing-Mode?", "JavaFX Error", JOptionPane.YES_NO_OPTION);
        }

        if(option == JOptionPane.YES_OPTION){
            Browser.start(Browser.Mode.SWING);

        } else {
            System.exit(0);
        }
    }

}
