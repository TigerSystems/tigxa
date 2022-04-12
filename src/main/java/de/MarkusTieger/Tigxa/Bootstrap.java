package de.MarkusTieger.Tigxa;

import javafx.application.Application;
import javafx.stage.Stage;

public class Bootstrap extends Application {

    private static ClassLoader loader = null;
    private static final String START_CLASS = "de.MarkusTieger.Tigxa.Bootstrap";

    public static void main(String[] args) {
        loader = Thread.currentThread().getContextClassLoader();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Class.forName(START_CLASS, true, loader).getDeclaredMethod("start").invoke(null);
    }
}
