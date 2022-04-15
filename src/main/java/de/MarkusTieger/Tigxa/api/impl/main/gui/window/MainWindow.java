package de.MarkusTieger.Tigxa.api.impl.main.gui.window;

import de.MarkusTieger.Tigxa.api.gui.IScreen;
import de.MarkusTieger.Tigxa.api.web.IWebEngine;
import de.MarkusTieger.Tigxa.api.window.ITab;
import de.MarkusTieger.Tigxa.api.window.IWindow;
import de.MarkusTieger.Tigxa.api.window.TabType;
import de.MarkusTieger.Tigxa.gui.window.BrowserWindow;
import de.MarkusTieger.Tigxa.web.MainContent;

import java.awt.*;
import java.util.List;
import java.util.*;

public class MainWindow implements IWindow {

    private final MainWindowManager windowManager;
    public final BrowserWindow window;

    final Map<Component, ITab> map = Collections.synchronizedMap(new HashMap<>());

    public MainWindow(MainWindowManager windowManager, BrowserWindow window) {
        this.window = window;
        this.windowManager = windowManager;
    }

    @Override
    public ITab add(String url, boolean autoselect) {
        Component comp = window.newTab(url, autoselect);
        ITab tab = genTab(TabType.WEB, comp);
        synchronized (map) {
            map.put(comp, tab);
        }
        return tab;
    }

    @Override
    public ITab add(IScreen iScreen) {
        if (!windowManager.api.getGUIManager().verify(iScreen)) return null;

        Component comp = window.newTab(iScreen, true);
        ITab tab = genTab(TabType.SCREEN, comp);
        synchronized (map) {
            map.put(comp, tab);
        }
        return tab;
    }

    @Override
    public List<ITab> listTabs() {
        List<ITab> tabs = new ArrayList<>();
        synchronized (map) {
            for (int i = 0; i < window.tabs.getTabCount(); i++) {
                Component comp = window.tabs.getComponent(i);
                ITab tab = map.get(comp);
                if (tab == null) {
                    tab = genTab(window.getTabLinks().get(comp) == null ? TabType.SCREEN : TabType.WEB, comp);
                    map.put(comp, tab);
                }
                tabs.add(tab);
            }
        }
        return tabs;
    }

    private ITab genTab(TabType type, Component comp) {
        return new MainTab(this, type, comp);
    }

    @Override
    public void remove() {
        if (isActive()) {
            windowManager.map.remove(window, this);
            window.close();
        }
    }

    @Override
    public boolean isActive() {
        return windowManager.map.containsValue(this);
    }

    @Override
    public ITab getSelectedTab() {
        synchronized (map) {
            Component comp = window.tabs.getSelectedComponent();
            ITab tab = map.get(comp);
            if (tab != null) return tab;
            tab = genTab(window.getTabLinks().get(comp) == null ? TabType.SCREEN : TabType.WEB, comp);
            map.put(comp, tab);
            return tab;
        }
    }

    @Override
    public ITab setSelectedTab(ITab tab) {
        synchronized (map) {
            for (Map.Entry<Component, ITab> e : map.entrySet()) {
                if (e.getValue() == tab) {
                    window.tabs.setSelectedComponent(e.getKey());
                }
            }
        }
        return tab;
    }

    public BrowserWindow getHandler() {
        return window;
    }

    public ITab fromHandler(IWebEngine data) {
        Map<Component, IWebEngine> tabLinks = window.getTabLinks();
        Component c = null;
        synchronized (tabLinks) {
            for (Map.Entry<Component, IWebEngine> e : tabLinks.entrySet()) {
                if (e.getValue().equals(data)) {
                    c = e.getKey();
                    break;
                }
            }
        }
        if (c == null) return null;

        synchronized (map) {
            ITab tab = map.get(c);
            if (tab == null) {
                tab = genTab(window.getTabLinks().get(c) == null ? TabType.SCREEN : TabType.WEB, c);
                map.put(c, tab);
            }
            return tab;
        }
    }
}
