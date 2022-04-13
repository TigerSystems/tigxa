package de.MarkusTieger.Tigxa.api.impl.main;

import de.MarkusTieger.Tigxa.api.IAPI;
import de.MarkusTieger.Tigxa.api.action.IActionHandler;
import de.MarkusTieger.Tigxa.api.event.IEventManager;
import de.MarkusTieger.Tigxa.api.gui.IGUIManager;
import de.MarkusTieger.Tigxa.api.impl.main.gui.MainGuiManager;
import de.MarkusTieger.Tigxa.api.impl.main.gui.window.MainWindowManager;
import de.MarkusTieger.Tigxa.api.impl.main.web.MainWebManager;
import de.MarkusTieger.Tigxa.api.permission.IPermissionManager;
import de.MarkusTieger.Tigxa.api.web.IWebManager;
import de.MarkusTieger.Tigxa.api.window.IWindowManager;
import de.MarkusTieger.Tigxa.extension.IExtension;

import java.io.File;

public class MainAPI implements IAPI {

    private final IWindowManager window;
    private final IPermissionManager perm;
    private final IEventManager event;
    private final IGUIManager gui;
    private final IWebManager web;

    public MainAPI(File configRoot) {
        perm = new MainPermManager();
        window = new MainWindowManager(this, configRoot);
        gui = new MainGuiManager();
        event = new MainEventManager(perm);
        web = new MainWebManager(this);
    }

    @Override
    public IWindowManager getWindowManager() {
        return window;
    }

    @Override
    public IEventManager getEventManager() {
        return event;
    }

    @Override
    public IWebManager getWebManager() {
        return web;
    }

    @Override
    public IGUIManager getGUIManager() {
        return gui;
    }

    @Override
    public IActionHandler getActionHandler() {
        return null;
    }

    @Override
    public IPermissionManager getPermissionManager() {
        return perm;
    }

    @Override
    public IExtension getExtension() {
        return null;
    }
}
