package de.MarkusTieger.Tigxa.api.impl.main.gui.context;

import de.MarkusTieger.Tigxa.api.action.IActionHandler;
import de.MarkusTieger.Tigxa.api.gui.IGUIWindow;
import de.MarkusTieger.Tigxa.api.gui.context.IContextEntry;
import de.MarkusTieger.Tigxa.api.gui.context.IContextMenu;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import lombok.Getter;

public class MainFXContextMenu implements IContextMenu {

    private final IActionHandler action;
    private final ContextMenu ctx;

    @Getter
    private IGUIWindow lastWindow = null;

    public MainFXContextMenu(IActionHandler action) {
        this.action = action;
        this.ctx = new ContextMenu();
        // ThemeManager.applyStyle(ctx);
    }

    @Override
    public IContextEntry addEntry(String name, String actionId, boolean allowSubs) {
        MenuItem item = allowSubs ? new Menu(name) : new MenuItem(name);
        item.setOnAction((e) -> action.onAction(lastWindow, actionId));

        ctx.getItems().add(item);

        return allowSubs ? new MainFXContextEntry(this, action, (Menu) item) : null;
    }

    @Override
    public void addSeperator() {
        SeparatorMenuItem item = new SeparatorMenuItem();
        ctx.getItems().add(item);
    }

    @Override
    public void show(IGUIWindow window, int x, int y) {
        lastWindow = window;
        // Platform.runLater(() -> ctx.show(window.asAnchor(), x, y));
    }
}
