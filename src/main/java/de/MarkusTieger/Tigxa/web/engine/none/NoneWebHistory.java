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
    public void backward() {

    }

    @Override
    public void forward() {

    }
}
