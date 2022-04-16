package de.MarkusTieger.Tigxa.api.impl.main.media;

import de.MarkusTieger.Tigxa.api.IAPI;
import de.MarkusTieger.Tigxa.api.engine.IEngine;
import de.MarkusTieger.Tigxa.api.impl.main.gui.window.MainTab;
import de.MarkusTieger.Tigxa.api.impl.main.gui.window.MainWindow;
import de.MarkusTieger.Tigxa.api.media.IMediaEngine;
import de.MarkusTieger.Tigxa.api.media.IMediaManager;
import de.MarkusTieger.Tigxa.api.web.IWebEngine;
import de.MarkusTieger.Tigxa.api.window.ITab;
import de.MarkusTieger.Tigxa.api.window.IWindow;
import de.MarkusTieger.Tigxa.api.window.TabType;
import javafx.scene.web.WebView;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainMediaManager implements IMediaManager {

    private final IAPI api;

    public MainMediaManager(IAPI api) {
        this.api = api;
    }

    private final Map<WebView, IMediaEngine> map = Collections.synchronizedMap(new HashMap<>());

    @Override
    public IMediaEngine getEngineByTab(ITab iTab) {
        if (iTab.getType() != TabType.MEDIA) return null;

        IWindow window = iTab.getWindow();
        if (!api.getWindowManager().listWindows().contains(window)) return null;
        if (!window.listTabs().contains(iTab)) return null;

        Map<Component, IEngine> map = ((MainWindow)window).window.getTabLinks();
        synchronized (map){
            return (IMediaEngine) map.get(((MainTab)iTab).getComp());
        }
    }

    @Override
    public IMediaEngine getEngineFromCurrentTab(IWindow iWindow) {
        return getEngineByTab(iWindow.getSelectedTab());
    }
}
