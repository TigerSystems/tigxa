package de.markustieger.tigxa.extension.api.impl.main.gui.window;

import de.markustieger.tigxa.extension.api.window.ITab;
import de.markustieger.tigxa.extension.api.window.IWindow;
import de.markustieger.tigxa.gui.window.BrowserWindow;

import java.awt.*;
import java.util.List;
import java.util.*;

public class MainWindow implements IWindow {

    private final MainWindowManager windowManager;
    final BrowserWindow window;

    final Map<Component, ITab> map = Collections.synchronizedMap(new HashMap<>());

    public MainWindow(MainWindowManager windowManager, BrowserWindow window) {
        this.window = window;
        this.windowManager = windowManager;
    }

    @Override
    public ITab add(String url, boolean autoselect) {
        Component comp = window.newTab(url, autoselect);
        ITab tab = genTab(comp);
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
                    tab = genTab(comp);
                    map.put(comp, tab);
                }
                tabs.add(tab);
            }
        }
        return tabs;
    }

    private ITab genTab(Component comp) {
        return new MainTab(this, comp);
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
            tab = genTab(comp);
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
}
