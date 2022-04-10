package de.markustieger.tigxa.extension.api.impl.main.gui;

import de.markustieger.tigxa.extension.api.action.IActionHandler;
import de.markustieger.tigxa.extension.api.gui.IGUIManager;
import de.markustieger.tigxa.extension.api.gui.context.IContextMenu;
import de.markustieger.tigxa.extension.api.impl.main.gui.context.MainContextMenu;

public class MainGuiManager implements IGUIManager {

    @Override
    public IContextMenu createContextMenu(boolean fxthread, IActionHandler action) {
        return new MainContextMenu(action, fxthread);
    }

}
