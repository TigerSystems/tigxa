package de.markustieger.tigxa.extension.api.gui;

import de.markustieger.tigxa.extension.api.action.IActionHandler;
import de.markustieger.tigxa.extension.api.gui.context.IContextMenu;

public interface IGUIManager {

    IContextMenu createContextMenu(boolean fxthread, IActionHandler action);

}
