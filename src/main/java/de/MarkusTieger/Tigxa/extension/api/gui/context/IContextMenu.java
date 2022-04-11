package de.MarkusTieger.Tigxa.extension.api.gui.context;

import de.MarkusTieger.Tigxa.extension.api.gui.IGUIWindow;

public interface IContextMenu extends IContextEntry {

    void show(IGUIWindow window, int x, int y);

}
