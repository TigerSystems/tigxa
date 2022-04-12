package de.MarkusTieger.Tigxa.api.impl;

import de.MarkusTieger.Tigxa.api.gui.IGUIWindow;
import de.MarkusTieger.Tigxa.api.window.IWindow;

public class DefaultGUIWindow implements IGUIWindow {

    private final IWindow window;

    public DefaultGUIWindow(IWindow window) {
        this.window = window;
    }

    @Override
    public IWindow asWindow() {
        return window;
    }
}
