package de.MarkusTieger.Tigxa.web.engine.none;

import de.MarkusTieger.Tigxa.api.web.IWebHistory;

public class NoneWebHistory implements IWebHistory {

    @Override
    public boolean hasBackwards() {
        return false;
    }

    @Override
    public boolean hasForwards() {
        return false;
    }

    @Override
    public int getCurrentIndex() {
        return 0;
    }

    @Override
    public String get(int i) {
        return null;
    }

    @Override
    public String go(int i) {
        return null;
    }
}
