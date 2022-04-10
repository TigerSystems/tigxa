package de.markustieger.tigxa.extension.api.impl.main;

import de.markustieger.tigxa.extension.api.IAPI;
import de.markustieger.tigxa.extension.api.action.IActionHandler;
import de.markustieger.tigxa.extension.api.event.IEventManager;
import de.markustieger.tigxa.extension.api.gui.IGUIManager;
import de.markustieger.tigxa.extension.api.impl.main.gui.MainGuiManager;
import de.markustieger.tigxa.extension.api.impl.main.gui.window.MainWindowManager;
import de.markustieger.tigxa.extension.api.permission.IPermissionManager;
import de.markustieger.tigxa.extension.api.window.IWindowManager;

import java.io.File;

public class MainAPI implements IAPI {

    private final IWindowManager window;
    private final IGUIManager gui;

    public MainAPI(File configRoot) {
        window = new MainWindowManager(this, configRoot);
        gui = new MainGuiManager();
    }

    @Override
    public IWindowManager getWindowManager() {
        return window;
    }

    @Override
    public IEventManager getEventManager() {
        return null;
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
        return null;
    }
}
