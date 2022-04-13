package de.MarkusTieger.Tigxa.api.impl.main.web;

import de.MarkusTieger.Tigxa.api.impl.main.gui.window.MainWindow;
import de.MarkusTieger.Tigxa.api.web.IWebEngine;
import de.MarkusTieger.Tigxa.events.WebLoadStartEvent;
import de.MarkusTieger.Tigxa.web.MainContent;
import javafx.application.Platform;
import javafx.print.PrinterJob;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;

import java.awt.*;
import java.util.Map;

public class MainWebEngine implements IWebEngine {

    private final MainWebManager manager;

    private final WebView data;

    public MainWebEngine(MainWebManager manager, WebView data) {
        this.manager = manager;
        this.data = data;
    }

    @Override
    public void setZoom(double factor) {
        if (data != null) data.setZoom(factor);
    }

    @Override
    public void print() {
        if (data != null) {
            Platform.runLater(() -> {
                PrinterJob job = PrinterJob.createPrinterJob();
                if (!job.showPrintDialog(null)) return;
                data.getEngine().print(job);
            });
        }
    }

    @Override
    public void load(String s) {
        if (data != null) {
            WebLoadStartEvent event = new WebLoadStartEvent(this, s, false);
            if (!event.isCanceled())
                Platform.runLater(() -> {
                    data.getEngine().load(event.getLocation());
                });
        }
    }

    @Override
    public Document getDocument() {
        if (data != null) {
            return data.getEngine().getDocument();
        }
        return null;
    }

    @Override
    public Object executeScript(String script) {
        return data.getEngine().executeScript(script);
    }
}
