package de.MarkusTieger.Tigxa.web.engine.fx;

import de.MarkusTieger.Tigxa.api.web.IWebEngine;
import de.MarkusTieger.Tigxa.api.web.IWebHistory;
import de.MarkusTieger.Tigxa.events.WebLoadStartEvent;
import javafx.application.Platform;
import javafx.print.PrinterJob;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;

public class FXWebEngine implements IWebEngine {

    private final WebView data;
    private final IWebHistory history;

    public FXWebEngine(WebView data) {
        this.data = data;
        this.history = new FXWebHistory(data.getEngine().getHistory());
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
    public void reload(){
        if(data != null){
            Platform.runLater(data.getEngine()::reload);
        }
    }

    @Override
    public String getLocation() {
        if(data != null) return data.getEngine().getLocation();
        return null;
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

    @Override
    public IWebHistory getHistory() {
        return history;
    }

    @Override
    public void loadContent(String s, String s1) {
        if(data != null){
            Platform.runLater(() -> data.getEngine().loadContent(s, s1));
        }
    }

    public WebView getHandler() {
        return data;
    }
}
