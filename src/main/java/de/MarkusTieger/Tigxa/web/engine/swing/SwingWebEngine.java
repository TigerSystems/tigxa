package de.MarkusTieger.Tigxa.web.engine.swing;

import de.MarkusTieger.Tigxa.api.web.IWebEngine;
import de.MarkusTieger.Tigxa.api.web.IWebHistory;
import org.w3c.dom.Document;

import javax.swing.*;
import java.io.IOException;

public class SwingWebEngine implements IWebEngine {

    private final JEditorPane editor;

    public SwingWebEngine(JEditorPane editor){
        this.editor = editor;
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
        try {
            editor.setPage(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        editor.setContentType(s1);
        editor.setText(s);
    }

    @Override
    public void reload() {
        try {
            editor.setPage(editor.getPage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLocation() {
        return editor.getPage().toString();
    }
}
