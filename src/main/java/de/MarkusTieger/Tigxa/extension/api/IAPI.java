package de.MarkusTieger.Tigxa.extension.api;

import de.MarkusTieger.Tigxa.extension.api.action.IActionHandler;
import de.MarkusTieger.Tigxa.extension.api.event.IEventManager;
import de.MarkusTieger.Tigxa.extension.api.gui.IGUIManager;
import de.MarkusTieger.Tigxa.extension.api.permission.IPermissionManager;
import de.MarkusTieger.Tigxa.extension.api.window.IWindowManager;

public interface IAPI {

    IWindowManager getWindowManager();

    IGUIManager getGUIManager();

    IActionHandler getActionHandler();

    IEventManager getEventManager();

    IPermissionManager getPermissionManager();
}
