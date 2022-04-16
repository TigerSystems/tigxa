package de.MarkusTieger.Tigxa.extensions.impl.external;

import de.MarkusTieger.Tigxa.api.IAPI;
import de.MarkusTieger.Tigxa.api.event.IEvent;
import de.MarkusTieger.Tigxa.api.permission.IPermissionManager;
import de.MarkusTieger.Tigxa.api.window.IWindow;
import de.MarkusTieger.Tigxa.extension.impl.BasicExtension;
import de.MarkusTieger.Tigxa.extensions.impl.impl.JavaExtension;
import javafx.application.Platform;

import java.net.URL;

public class JarExtension extends BasicExtension {

    private final IAPI api;
    private final JavaExtension ext;

    public JarExtension(JavaExtension ext, IAPI api, String name, String version, String[] authors, URL path) {
        super(api.getPermissionManager(), name, version, authors, path);
        this.api = api;
        this.ext = ext;
    }

    @Override
    public void onLoad() {
        Platform.runLater(ext::onLoad);
    }

    @Override
    public void onEnable() {
        Platform.runLater(ext::onEnable);
    }

    @Override
    public void onDisable() {
        Platform.runLater(ext::onDisable);
    }

    @Override
    public void onAction(IWindow iWindow, int i, int i1, int i2, int i3) {
        Platform.runLater(() -> ext.onAction(iWindow, i, i1, i2, i3));
    }

    @Override
    public void onAction(IWindow iWindow, String s) {
        Platform.runLater(() -> ext.onAction(iWindow, s));
    }

    @Override
    public void onEvent(IEvent iEvent) {
        Platform.runLater(() -> ext.onEvent(iEvent));
    }
}
