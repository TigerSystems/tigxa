package de.MarkusTieger.Tigxa.extension.api.impl.main.gui;

import de.MarkusTieger.Tigxa.extension.api.action.IActionHandler;
import de.MarkusTieger.Tigxa.extension.api.impl.main.gui.context.MainContextMenu;
import de.MarkusTieger.Tigxa.extension.api.gui.IGUIManager;
import de.MarkusTieger.Tigxa.extension.api.gui.context.IContextMenu;

public class MainGuiManager implements IGUIManager {

    @Override
    public IContextMenu createContextMenu(boolean fxthread, IActionHandler action) {
        return new MainContextMenu(action, fxthread);
    }

}
