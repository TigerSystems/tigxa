package de.MarkusTieger.Tigxa.bootstrap;

import de.MarkusTieger.Tigxa.Browser;

import javax.swing.*;
import java.lang.reflect.Method;

public class Bootstrap {

    public static void main(String[] args) {

        System.out.println("Bootstrap is launching Application...");

        String force_launch = System.getProperty("tigxa.force_launch");

        if(force_launch == null || force_launch.equalsIgnoreCase("-")){
            try {
                System.out.println("Checking JavaFX integrity using \"" + Method.class.getPackage().getName() + "\"...");
                Class<?> clazz = Class.forName("javafx.application.Application");
                System.out.println("Launching...");
                try {
                    FXLauncher.main(args);
                } catch (Throwable e){
                    e.printStackTrace();
                    ErrorLauncher.launch(LaunchError.FX_NOT_LOADED);
                }
            } catch (ClassNotFoundException e) {
                ErrorLauncher.launch(LaunchError.FX_NOT_FOUND);
            }
        } else {

            System.out.println("Using Force-Launch...");

            Browser.Mode mode = Browser.Mode.valueOf(force_launch.toUpperCase());
            if(mode == null){

                System.out.println("Invalid Force-Launch-Property! Showing Option-Chooser...");

                int option = JOptionPane.showConfirmDialog(null, "Force-Launch failed. Would you like to Launch Normal?", "Force-Launch failed.", JOptionPane.YES_NO_OPTION);
                System.out.println("Option choosed.");
                if(option == JOptionPane.YES_OPTION){
                    System.out.println("Restarting Normal...");
                    System.setProperty("tigxa.force_launch", "-");
                    main(args);
                } else {
                    System.out.println("Stopping Bootstrap...");
                    System.exit(0);
                }
            } else {
                if(mode == Browser.Mode.JAVAFX){
                    System.out.println("Force-Launch-Property is set to Default! Restarting...");
                    System.setProperty("tigxa.force_launch", "-");
                    main(args);
                } else {
                    System.out.println("Launching using Engine \"" + mode.name() + "\"...");
                    try {
                        Browser.start(mode);
                    } catch(Throwable e){
                    }
                }
            }

        }
    }

}
