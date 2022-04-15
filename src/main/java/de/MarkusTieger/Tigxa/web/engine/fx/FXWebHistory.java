package de.MarkusTieger.Tigxa.web.engine.fx;

import de.MarkusTieger.Tigxa.api.web.IWebHistory;
import javafx.scene.web.WebHistory;

public class FXWebHistory implements IWebHistory {

    private final WebHistory history;

    public FXWebHistory(WebHistory history){
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
    public int getCurrentIndex() {
        return history.getCurrentIndex();
    }

    @Override
    public String get(int i) {
        return history.getEntries().get(getCurrentIndex() + i).getUrl();
    }

    @Override
    public String go(int i) {
        String loc = get(i);
        history.go(i);
        return loc;
    }
}
