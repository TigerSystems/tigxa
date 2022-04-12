package de.MarkusTieger.Tigxa.api.impl.main.gui.context;

import de.MarkusTieger.Tigxa.api.action.IActionHandler;
import de.MarkusTieger.Tigxa.api.gui.context.IContextEntry;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class MainFXContextEntry implements IContextEntry {

    private final IActionHandler action;
    private final Menu handler;
    private final MainFXContextMenu main;

    public MainFXContextEntry(MainFXContextMenu main, IActionHandler action, Menu handler) {
        this.action = action;
        this.handler = handler;
        this.main = main;
    }

    @Override
    public IContextEntry addEntry(String name, String actionId, boolean allowSubs) {
        MenuItem item = allowSubs ? new Menu(name) : new MenuItem(name);
        item.setOnAction((e) -> action.onAction(main.getLastWindow(), actionId));
        handler.getItems().add(item);
        return allowSubs ? new MainFXContextEntry(main, action, (Menu) item) : null;
    }

    @Override
    public void addSeperator() {
        SeparatorMenuItem item = new SeparatorMenuItem();
        handler.getItems().add(item);
    }
}
