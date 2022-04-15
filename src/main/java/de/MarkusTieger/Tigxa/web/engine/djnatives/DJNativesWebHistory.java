package de.MarkusTieger.Tigxa.web.engine.djnatives;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import de.MarkusTieger.Tigxa.api.web.IWebHistory;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class DJNativesWebHistory implements IWebHistory {

    private final JWebBrowser browser;

    public DJNativesWebHistory(JWebBrowser browser){
        this.browser = browser;
    }

    @Override
    public boolean hasBackwards() {
        boolean[] array = new boolean[1];
        syncExec(() -> {
            array[0] = browser.isBackNavigationEnabled();
        });
        return array[0];
    }

    @Override
    public boolean hasForwards() {
        boolean[] array = new boolean[1];
        syncExec(() -> {
            array[0] = browser.isForwardNavigationEnabled();
        });
        return array[0];
    }

    @Override
    public int getCurrentIndex() {
        return 0;
    }

    private void syncExec(final Runnable r) {
        try {
            if (EventQueue.isDispatchThread()) r.run();
            else EventQueue.invokeAndWait(r);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String get(int i) {
        return null;
    }

    @Override
    public String go(int i) {
        String[] data = new String[1];
        syncExec(() -> {
            if(i == 1) browser.navigateForward();
            if(i == -1) browser.navigateBack();
            data[0] = browser.getResourceLocation();
        });
        return data[0];
    }
}
