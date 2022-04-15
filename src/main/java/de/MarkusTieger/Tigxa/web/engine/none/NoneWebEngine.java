package de.MarkusTieger.Tigxa.web.engine.none;

import de.MarkusTieger.Tigxa.api.web.IWebEngine;
import de.MarkusTieger.Tigxa.api.web.IWebHistory;
import org.w3c.dom.Document;

public class NoneWebEngine implements IWebEngine {

    private final IWebHistory history;

    public NoneWebEngine(){
        history = new NoneWebHistory();
    }

    @Override
    public void setZoom(double v) {

    }

    @Override
    public void print() {

    }

    @Override
    public void load(String s) {

    }

    @Override
    public Document getDocument() {
        return null;
    }

    @Override
    public Object executeScript(String s) {
        return null;
    }

    @Override
    public IWebHistory getHistory() {
        return null;
    }

    @Override
    public void loadContent(String s, String s1) {

    }

    @Override
    public void reload() {

    }

    @Override
    public String getLocation() {
        return "https://none.example";
    }
}
