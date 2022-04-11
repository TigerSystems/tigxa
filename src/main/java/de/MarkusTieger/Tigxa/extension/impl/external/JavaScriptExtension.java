package de.MarkusTieger.Tigxa.extension.impl.external;

import de.MarkusTieger.Tigxa.api.IAPI;
import de.MarkusTieger.Tigxa.api.gui.IGUIWindow;
import de.MarkusTieger.Tigxa.extension.impl.BasicExtension;
import javafx.application.Platform;
import netscape.javascript.JSObject;

public class JavaScriptExtension extends BasicExtension {

    private final JSObject obj;
    private final IAPI api;

    public JavaScriptExtension(JSObject obj, IAPI api, String name, String version, String[] authors, String base64) {
        super(name, version, authors, base64);
        this.obj = obj;
        this.api = api;
    }

    public JSObject getScript() {
        return obj;
    }

    @Override
    public void onLoad() {
        Platform.runLater(() -> {
            try {
                obj.call("onLoad", api);
            } catch (Throwable e) {
            }
        });
    }

    @Override
    public void onEnable() {
        Platform.runLater(() -> {
            try {
                obj.call("onEnable", api);
            } catch (Throwable e) {
            }
        });
    }

    @Override
    public void onDisable() {
        Platform.runLater(() -> {
            try {
                obj.call("onDisable", api);
            } catch (Throwable e) {

            }
        });
    }

    @Override
    public void onAction(IGUIWindow window, int relativeX, int relativeY, int absoluteX, int absoluteY) {
        Platform.runLater(() -> {

            try {
                obj.call("onAction", api, window, relativeX, relativeY, absoluteX, absoluteY);
            } catch (Throwable e) {

            }

        });
    }

    @Override
    public void onAction(IGUIWindow window, String id) {
        Platform.runLater(() -> {
            try {
                obj.call("onAction", api, window, id);
            } catch (Throwable e) {

            }
        });
    }
}
