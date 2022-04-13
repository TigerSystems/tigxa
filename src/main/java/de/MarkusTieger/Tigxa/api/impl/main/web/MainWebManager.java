package de.MarkusTieger.Tigxa.api.impl.main.web;

import de.MarkusTieger.Tigxa.api.IAPI;
import de.MarkusTieger.Tigxa.api.impl.main.gui.window.MainTab;
import de.MarkusTieger.Tigxa.api.impl.main.gui.window.MainWindow;
import de.MarkusTieger.Tigxa.api.web.IWebEngine;
import de.MarkusTieger.Tigxa.api.web.IWebManager;
import de.MarkusTieger.Tigxa.api.window.ITab;
import de.MarkusTieger.Tigxa.api.window.IWindow;
import de.MarkusTieger.Tigxa.api.window.TabType;
import de.MarkusTieger.Tigxa.web.MainContent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainWebManager implements IWebManager {

    private final IAPI api;

    public MainWebManager(IAPI api) {
        this.api = api;
    }

    private final Map<WebView, IWebEngine> map = Collections.synchronizedMap(new HashMap<>());

    @Override
    public IWebEngine getEngineByTab(ITab iTab) {
        if (iTab.getType() != TabType.WEB) return null;
        IWindow window = iTab.getWindow();
        if (!api.getWindowManager().listWindows().contains(window)) return null;
        if (!window.listTabs().contains(iTab)) return null;

        Map<Component, MainContent.MainContentData> map = ((MainWindow)window).window.getTabLinks();
        synchronized (map){
            return fromHandler(map.get(((MainTab)iTab).getComp()).webView());
        }
    }

    private IWebEngine genEngine(WebView data) {
        MainWebEngine engine = new MainWebEngine(this, data);
        return engine;
    }

    @Override
    public IWebEngine getEngineFromCurrentTab(IWindow iWindow) {
        return getEngineByTab(iWindow.getSelectedTab());
    }

    public IWebEngine fromHandler(WebView data) {
        synchronized (map){
            IWebEngine engine = map.get(data);
            if(engine == null){
                engine = genEngine(data);
                map.put(data, engine);
            }
            return engine;
        }
    }
}
