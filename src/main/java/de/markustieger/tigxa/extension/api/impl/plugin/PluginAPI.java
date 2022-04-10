package de.markustieger.tigxa.extension.api.impl.plugin;

import de.markustieger.tigxa.extension.api.IAPI;
import de.markustieger.tigxa.extension.api.action.IActionHandler;
import de.markustieger.tigxa.extension.api.event.IEventManager;
import de.markustieger.tigxa.extension.api.gui.IGUIManager;
import de.markustieger.tigxa.extension.api.permission.IPermissionManager;
import de.markustieger.tigxa.extension.api.permission.Permission;
import de.markustieger.tigxa.extension.api.window.IWindowManager;

public class PluginAPI implements IAPI {

    private final IAPI parent;
    private final IPermissionManager permManager;
    private final IActionHandler action;

    public PluginAPI(IAPI parent, Permission[] perms, IActionHandler action) {
        this.parent = parent;
        this.permManager = new PluginPermManager(perms);
        this.action = action;
    }

    @Override
    public IWindowManager getWindowManager() {
        if (!getPermissionManager().hasPermission(Permission.WINDOW))
            throw new RuntimeException("The Plugin doesn't have the window permission!");
        return parent.getWindowManager();
    }

    @Override
    public IEventManager getEventManager() {
        return parent.getEventManager();
    }

    @Override
    public IGUIManager getGUIManager() {
        if (!getPermissionManager().hasPermission(Permission.GUI))
            throw new RuntimeException("The Plugin doesn't have the window permission!");
        return parent.getGUIManager();
    }

    @Override
    public IActionHandler getActionHandler() {
        return action;
    }

    @Override
    public IPermissionManager getPermissionManager() {
        return permManager;
    }

}
