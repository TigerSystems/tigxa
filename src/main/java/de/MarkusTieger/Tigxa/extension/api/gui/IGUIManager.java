package de.MarkusTieger.Tigxa.extension.api.gui;

import de.MarkusTieger.Tigxa.extension.api.action.IActionHandler;
import de.MarkusTieger.Tigxa.extension.api.gui.context.IContextMenu;

public interface IGUIManager {

    IContextMenu createContextMenu(boolean fxthread, IActionHandler action);

}
