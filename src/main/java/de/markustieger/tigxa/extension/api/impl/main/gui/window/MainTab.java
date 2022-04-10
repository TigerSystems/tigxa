package de.markustieger.tigxa.extension.api.impl.main.gui.window;

import de.markustieger.tigxa.extension.api.window.ITab;
import de.markustieger.tigxa.web.MainContent;
import javafx.application.Platform;
import javafx.print.PrinterJob;
import javafx.scene.web.WebEngine;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class MainTab implements ITab {

    private final MainWindow window;
    private final Component comp;

    public MainTab(MainWindow window, Component comp) {
        this.comp = comp;
        this.window = window;
    }

    @Override
    public String getTitle() {
        return window.window.tabs.getTitleAt(window.window.tabs.indexOfComponent(comp));
    }

    @Override
    public Icon getIcon() {
        return window.window.tabs.getIconAt(window.window.tabs.indexOfComponent(comp));
    }

    @Override
    public void remove() {
        if (isActive()) {
            window.map.remove(window, this);
            window.window.tabs.remove(window.window.tabs.indexOfComponent(comp));
            window.window.unloadTab(comp);
            window.window.update();
        }
    }

    @Override
    public void setZoom(double factor) {
        Map<Component, MainContent.MainContentData> tabLinks = window.window.getTabLinks();
        synchronized (tabLinks) {
            MainContent.MainContentData data = tabLinks.get(comp);
            if (data != null) data.webView().setZoom(factor);
        }
    }

    @Override
    public void print() {
        Map<Component, MainContent.MainContentData> tabLinks = window.window.getTabLinks();
        synchronized (tabLinks) {
            MainContent.MainContentData data = tabLinks.get(comp);
            if (data != null) {
                WebEngine engine = data.webEngine();
                Platform.runLater(() -> {
                    PrinterJob job = PrinterJob.createPrinterJob();
                    if (!job.showPrintDialog(null)) return;
                    data.webEngine().print(job);
                });
            }
        }
    }

    @Override
    public boolean isActive() {
        return window.map.containsValue(this);
    }
}
