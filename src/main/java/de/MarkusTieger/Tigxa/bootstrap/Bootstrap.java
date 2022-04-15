package de.MarkusTieger.Tigxa.bootstrap;

import de.MarkusTieger.Tigxa.Browser;

import javax.swing.*;

public class Bootstrap {

    public static void main(String[] args) {
        String force_launch = System.getProperty("tigxa.force_launch");

        if(force_launch == null || force_launch.equalsIgnoreCase("-")){
            try {
                Class<?> clazz = Class.forName("javafx.application.Application");
                try {
                    FXLauncher.main(args);
                } catch (Throwable e){
                    ErrorLauncher.launch(FXError.NOT_LOADED);
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                ErrorLauncher.launch(FXError.NOT_FOUND);
            }
        } else {

            Browser.Mode mode = Browser.Mode.valueOf(force_launch.toUpperCase());
            if(mode == null){
                int option = JOptionPane.showConfirmDialog(null, "Force-Launch failed. Would you like to Launch Normal?", "Force-Launch failed.", JOptionPane.YES_NO_OPTION);
                if(option == JOptionPane.YES_OPTION){
                    System.setProperty("tigxa.force_launch", "-");
                    main(args);
                } else {
                    System.exit(0);
                }
            }

        }
    }

}
