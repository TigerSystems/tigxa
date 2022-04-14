package de.MarkusTieger.Tigxa;

import javafx.application.Application;
import javafx.stage.Stage;

public class Bootstrap extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Browser.start();
    }
}
