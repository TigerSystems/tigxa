package de.MarkusTieger.Tigxa.bootstrap;

import de.MarkusTieger.Tigxa.Browser;
import javafx.application.Application;
import javafx.stage.Stage;

public class FXLauncher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Browser.start(Browser.Mode.JAVAFX);
        } catch(Throwable e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        launch(args);
    }
}
