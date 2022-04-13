package de.MarkusTieger.Tigxa.api.impl.extension;

import de.MarkusTieger.Tigxa.api.IAPI;
import de.MarkusTieger.Tigxa.api.action.IActionHandler;
import de.MarkusTieger.Tigxa.api.gui.IGUIManager;
import de.MarkusTieger.Tigxa.api.gui.IScreen;
import de.MarkusTieger.Tigxa.api.gui.context.IContextMenu;
import de.MarkusTieger.Tigxa.api.gui.registry.IScreenRegistry;
import de.MarkusTieger.Tigxa.api.impl.main.gui.screen.MainScreenRegistry;
import de.MarkusTieger.Tigxa.api.window.ITab;

public class ExtensionGuiManager implements IGUIManager {

    private final IAPI api;
    private final IScreenRegistry registry;

    public ExtensionGuiManager(IAPI api, IAPI parent){
        this.api = parent;
        this.registry = new ExtensionScreenRegistry(api, (MainScreenRegistry) parent.getGUIManager().getScreenRegistry());
    }

    @Override
    public IContextMenu createContextMenu(boolean b, IActionHandler iActionHandler) {
        return api.getGUIManager().createContextMenu(b, iActionHandler);
    }

    @Override
    public IScreen createScreen(String s, String s1) {
        return api.getGUIManager().createScreen(s, s1);
    }

    @Override
    public IScreen getScreenByTab(ITab iTab) {
        return api.getGUIManager().getScreenByTab(iTab);
    }

    @Override
    public boolean verify(IScreen iScreen) {
        return api.getGUIManager().verify(iScreen);
    }

    @Override
    public IScreenRegistry getScreenRegistry() {
        return registry;
    }
}
