package de.MarkusTieger.Tigxa.api.impl.main.gui;

import de.MarkusTieger.Tigxa.api.action.IActionHandler;
import de.MarkusTieger.Tigxa.api.gui.IGUIManager;
import de.MarkusTieger.Tigxa.api.gui.IScreen;
import de.MarkusTieger.Tigxa.api.gui.context.IContextMenu;
import de.MarkusTieger.Tigxa.api.impl.DefaultGuiScreen;
import de.MarkusTieger.Tigxa.api.impl.main.gui.context.MainContextMenu;
import de.MarkusTieger.Tigxa.api.impl.main.gui.window.MainTab;
import de.MarkusTieger.Tigxa.api.window.ITab;
import de.MarkusTieger.Tigxa.api.window.TabType;

import java.awt.*;
import java.util.List;
import java.util.*;

public class MainGuiManager implements IGUIManager {

    private final List<IScreen> screens = Collections.synchronizedList(new ArrayList<>());
    private final Map<ITab, IScreen> map = Collections.synchronizedMap(new HashMap<>());

    @Override
    public IContextMenu createContextMenu(boolean fxthread, IActionHandler action) {
        return new MainContextMenu(action, fxthread);
    }

    @Override
    public IScreen createScreen(String title, String location) {
        DefaultGuiScreen screen = new DefaultGuiScreen(title, location);
        synchronized (screens) {
            screens.add(screen);
        }
        return screen;
    }

    @Override
    public IScreen getScreenByTab(ITab iTab) {
        synchronized (map) {
            IScreen screen = map.get(iTab);
            if (screen != null) return screen;
            if (iTab.getType() != TabType.SCREEN) return null;
            if (iTab instanceof MainTab mt) {
                Component c = mt.getComp();
                synchronized (screens) {
                    for (IScreen sc : screens) {
                        if (c == sc.getContentPane()) {
                            map.put(iTab, sc);
                            return sc;
                        }
                    }
                }
            }
            return null;
        }
    }

    @Override
    public boolean verify(IScreen iScreen) {
        synchronized (screens) {
            return screens.contains(iScreen);
        }
    }

}
