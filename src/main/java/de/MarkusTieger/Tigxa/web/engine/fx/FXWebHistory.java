package de.MarkusTieger.Tigxa.web.engine.fx;

import de.MarkusTieger.Tigxa.api.web.IWebHistory;
import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;

public class FXWebHistory implements IWebHistory {

    private final WebEngine engine;
    private final WebHistory history;

    public FXWebHistory(WebEngine engine, WebHistory history){
        this.engine = engine;
        this.history = history;
    }

    @Override
    public boolean hasBackwards() {
        return history.getCurrentIndex() > 0;
    }

    @Override
    public boolean hasForwards() {
        return (history.getCurrentIndex() + 1) < history.getEntries().size();
    }

    @Override
    public void backward() {
        String url = history.getEntries().get(history.getCurrentIndex() - 1).getUrl();
        Platform.runLater(() -> {
            engine.load(url);
            history.go(-1);
        });
    }

    @Override
    public void forward() {
        String url = history.getEntries().get(history.getCurrentIndex() + 1).getUrl();
        Platform.runLater(() -> {
            engine.load(url);
            history.go(1);
        });
    }
}
