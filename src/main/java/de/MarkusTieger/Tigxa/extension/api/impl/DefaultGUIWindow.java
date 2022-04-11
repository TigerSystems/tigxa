package de.MarkusTieger.Tigxa.extension.api.impl;

import de.MarkusTieger.Tigxa.extension.api.window.IWindow;
import de.MarkusTieger.Tigxa.extension.api.gui.IGUIWindow;

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
