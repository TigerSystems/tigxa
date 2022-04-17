package de.MarkusTieger.Tigxa.bootstrap;

import de.MarkusTieger.Tigxa.Browser;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

public class FXLauncher extends Application {

    private static final Logger LOGGER = Logger.getLogger(FXLauncher.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            LOGGER.info("Launching...");
            Browser.start(Browser.Mode.JAVAFX);
        } catch(Throwable e){
            LOGGER.warn("Launch Failed. Starting Error-Launcher...", e);
            ErrorLauncher.launch(LaunchError.FX_NOT_FOUND);
        }
    }

    public static void main(String[] args){
        launch(args);
    }
}
