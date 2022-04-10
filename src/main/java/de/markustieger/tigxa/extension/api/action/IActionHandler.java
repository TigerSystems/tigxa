package de.markustieger.tigxa.extension.api.action;

import de.markustieger.tigxa.extension.api.gui.IGUIWindow;

public interface IActionHandler {

    void onAction(IGUIWindow window, String id);

}
