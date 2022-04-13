package de.MarkusTieger.Tigxa.api.impl.main.web;

import de.MarkusTieger.Tigxa.api.impl.main.gui.window.MainWindow;
import de.MarkusTieger.Tigxa.api.web.IWebEngine;
import de.MarkusTieger.Tigxa.events.WebLoadStartEvent;
import de.MarkusTieger.Tigxa.web.MainContent;
import javafx.application.Platform;
import javafx.print.PrinterJob;
import org.w3c.dom.Document;

import java.awt.*;
import java.util.Map;

public class MainWebEngine implements IWebEngine {

    private final MainWebManager manager;
    private final MainWindow window;
    private final Component comp;
    private final MainContent.MainContentData data;

    public MainWebEngine(MainWebManager manager, MainWindow window, Component comp, MainContent.MainContentData data) {
        this.manager = manager;
        this.window = window;
        this.comp = comp;
        this.data = data;
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
                Platform.runLater(() -> {
                    PrinterJob job = PrinterJob.createPrinterJob();
                    if (!job.showPrintDialog(null)) return;
                    data.webEngine().print(job);
                });
            }
        }
    }

    @Override
    public void load(String s) {
        Map<Component, MainContent.MainContentData> tabLinks = window.window.getTabLinks();
        synchronized (tabLinks) {
            MainContent.MainContentData data = tabLinks.get(comp);
            if (data != null) {
                WebLoadStartEvent event = new WebLoadStartEvent(s, false);
                if (!event.isCanceled())
                    Platform.runLater(() -> {
                        data.webEngine().load(event.getLocation());
                    });
            }
        }
    }

    @Override
    public Document getDocument() {
        Map<Component, MainContent.MainContentData> tabLinks = window.window.getTabLinks();
        synchronized (tabLinks) {
            MainContent.MainContentData data = tabLinks.get(comp);
            if (data != null) {
                return data.webEngine().getDocument();
            }
        }
        return null;
    }
}
