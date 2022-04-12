package de.MarkusTieger.Tigxa.api.impl.main.gui;

import de.MarkusTieger.Tigxa.api.action.IActionHandler;
import de.MarkusTieger.Tigxa.api.gui.IGUIManager;
import de.MarkusTieger.Tigxa.api.gui.context.IContextMenu;
import de.MarkusTieger.Tigxa.api.impl.main.gui.context.MainContextMenu;

public class MainGuiManager implements IGUIManager {

    @Override
    public IContextMenu createContextMenu(boolean fxthread, IActionHandler action) {
        return new MainContextMenu(action, fxthread);
    }

}
