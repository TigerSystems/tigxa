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

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainWebManager implements IWebManager {

    private final IAPI api;

    public MainWebManager(IAPI api){
        this.api = api;
    }

    private final Map<ITab, IWebEngine> map = Collections.synchronizedMap(new HashMap<>());

    @Override
    public IWebEngine getEngineByTab(ITab iTab) {
        if(iTab.getType() != TabType.WEB) return null;
        IWindow window = iTab.getWindow();
        if(!api.getWindowManager().listWindows().contains(window)) return null;
        if(!window.listTabs().contains(iTab)) return null;
        synchronized (map){
            IWebEngine engine = map.get(iTab);
            if(engine == null){
                engine = genEngine(iTab);
                map.put(iTab, engine);
            }
            return engine;
        }
    }

    private IWebEngine genEngine(ITab iTab) {
        IWindow w = iTab.getWindow();
        if(!(w instanceof MainWindow mw)) return null;
        if(!(iTab instanceof MainTab mt)) return null;

        Map<Component, MainContent.MainContentData> map = mw.window.getTabLinks();
        synchronized (map){
            MainContent.MainContentData data = map.get(mt.getComp());
            MainWebEngine engine = new MainWebEngine(this, mw, mt.getComp(), data);
            return engine;
        }
    }

    @Override
    public IWebEngine getEngineFromCurrentTab(IWindow iWindow) {
        return null;
    }
}
