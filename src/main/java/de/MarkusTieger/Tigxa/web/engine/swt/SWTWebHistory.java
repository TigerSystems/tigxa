package de.MarkusTieger.Tigxa.web.engine.swt;

import de.MarkusTieger.Tigxa.api.web.IWebHistory;
import org.eclipse.swt.browser.Browser;

public class SWTWebHistory implements IWebHistory {

    private final Browser browser;

    public SWTWebHistory(Browser browser) {
        this.browser = browser;
    }


    @Override
    public boolean hasBackwards() {
        return browser.isBackEnabled() && false;
    }

    @Override
    public boolean hasForwards() {
        return browser.isForwardEnabled() && false; // TODO: WebHistory
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
