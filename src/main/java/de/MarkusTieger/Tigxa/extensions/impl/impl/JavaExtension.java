package de.MarkusTieger.Tigxa.extensions.impl.impl;

import de.MarkusTieger.Tigxa.api.event.IEvent;
import de.MarkusTieger.Tigxa.api.window.IWindow;

public class JavaExtension {

    public void onEnable() {}

    public void onDisable() {}

    public void onLoad() {}

    public void onAction(IWindow iWindow, int absoluteX, int absoluteY, int relativeX, int relativeY) {}

    public void onAction(IWindow iWindow, String s) {}

    public void onEvent(IEvent iEvent) {}

}
