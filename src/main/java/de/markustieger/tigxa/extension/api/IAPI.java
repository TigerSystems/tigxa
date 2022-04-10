package de.markustieger.tigxa.extension.api;

import de.markustieger.tigxa.extension.api.action.IActionHandler;
import de.markustieger.tigxa.extension.api.event.IEventManager;
import de.markustieger.tigxa.extension.api.gui.IGUIManager;
import de.markustieger.tigxa.extension.api.permission.IPermissionManager;
import de.markustieger.tigxa.extension.api.window.IWindowManager;

public interface IAPI {

    IWindowManager getWindowManager();

    IGUIManager getGUIManager();

    IActionHandler getActionHandler();

    IEventManager getEventManager();

    IPermissionManager getPermissionManager();
}
