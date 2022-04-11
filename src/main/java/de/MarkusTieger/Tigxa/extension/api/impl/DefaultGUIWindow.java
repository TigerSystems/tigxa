package de.MarkusTieger.Tigxa.extension.api.impl;

import de.MarkusTieger.Tigxa.extension.api.gui.IGUIWindow;
import de.MarkusTieger.Tigxa.extension.api.window.IWindow;

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
