package de.markustieger.tigxa.extension.api.impl;

import de.markustieger.tigxa.extension.api.gui.IGUIWindow;
import de.markustieger.tigxa.extension.api.window.IWindow;

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
