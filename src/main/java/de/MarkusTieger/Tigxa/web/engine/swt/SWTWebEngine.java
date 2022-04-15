package de.MarkusTieger.Tigxa.web.engine.swt;

import de.MarkusTieger.Tigxa.api.web.IWebEngine;
import de.MarkusTieger.Tigxa.api.web.IWebHistory;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.GC;
import org.w3c.dom.Document;

public class SWTWebEngine implements IWebEngine {

    private final Browser browser;
    private final SWTWebHistory history;

    public SWTWebEngine(Browser browser){
        this.browser = browser;
        this.history = new SWTWebHistory(browser);
    }

    @Override
    public void setZoom(double v) {

    }

    @Override
    public void print() {
        // TODO: Print
    }

    @Override
    public void load(String s) {
        browser.setUrl(s);
    }

    @Override
    public Document getDocument() {
        return null; // TODO: Document
    }

    @Override
    public Object executeScript(String s) {
        return browser.execute(s);
    }

    @Override
    public IWebHistory getHistory() {
        return history;
    }

    @Override
    public void loadContent(String s, String s1) {
        browser.setText(s);
    }

    @Override
    public void reload() {
        browser.refresh();
    }

    @Override
    public String getLocation() {
        return browser.getUrl();
    }

}
