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
        return browser.isBackEnabled();
    }

    @Override
    public boolean hasForwards() {
        return browser.isForwardEnabled();
    }

    @Override
    public void backward() {
        browser.back();
    }

    @Override
    public void forward() {
        browser.forward();
    }

}
