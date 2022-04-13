package de.MarkusTieger.Tigxa.extensions.impl.internal;

import de.MarkusTieger.Tigxa.Browser;
import de.MarkusTieger.Tigxa.api.IAPI;
import de.MarkusTieger.Tigxa.api.event.IEvent;
import de.MarkusTieger.Tigxa.api.permission.IPermissionManager;
import de.MarkusTieger.Tigxa.api.window.IWindow;
import de.MarkusTieger.Tigxa.events.WebStateChangedEvent;
import de.MarkusTieger.Tigxa.extension.impl.BasicExtension;
import javafx.concurrent.Worker;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AdblockerExtension extends BasicExtension {

    private final IAPI api;

    public AdblockerExtension(IAPI api) {
        super(api.getPermissionManager(), Browser.NAME + " Adblocker", Browser.FULL_VERSION, new String[] {Browser.AUTHOR}, Browser.class.getResource("/res/gui/extensions/adblocker.png"));
        this.api = api;
    }

    private String script = "window";

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

        try {
            InputStream in = AdblockerExtension.class.getResourceAsStream("/res/scripts/adblocker.js");
            script = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            in.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onAction(IWindow iWindow, int i, int i1, int i2, int i3) {

    }

    @Override
    public void onAction(IWindow iWindow, String s) {

    }

    @Override
    public void onEvent(IEvent iEvent) {
        if(iEvent instanceof WebStateChangedEvent se){
            if(se.getNewState() == Worker.State.SUCCEEDED){
                se.getEngine().executeScript(script);
            }
        }
    }
}
