package de.markustieger.tigxa.extension.api.gui.context;

import de.markustieger.tigxa.extension.api.gui.IGUIWindow;

public interface IContextMenu extends IContextEntry {

    void show(IGUIWindow window, int x, int y);

}
