package de.MarkusTieger.Tigxa.api.impl.main.gui.context;

import de.MarkusTieger.Tigxa.api.action.IActionHandler;
import de.MarkusTieger.Tigxa.api.gui.IGUIWindow;
import de.MarkusTieger.Tigxa.api.gui.context.IContextEntry;
import de.MarkusTieger.Tigxa.api.gui.context.IContextMenu;
import de.MarkusTieger.Tigxa.api.impl.main.gui.window.MainWindow;
import de.MarkusTieger.Tigxa.api.window.IWindow;
import javafx.application.Platform;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class MainContextMenu implements IContextMenu {

    private final IActionHandler action;
    private final JPopupMenu ctx;

    @Getter
    private IGUIWindow lastWindow = null;

    @Getter
    private boolean fxthread = false;

    public MainContextMenu(IActionHandler action, boolean fxthread) {
        this.action = action;
        this.ctx = new JPopupMenu();
        this.fxthread = fxthread;

        // ThemeManager.applyStyle(ctx);
    }

    @Override
    public IContextEntry addEntry(String name, String actionId, boolean allowSubs) {
        JMenuItem item = allowSubs ? new JMenu(name) : new JMenuItem(name);
        item.addActionListener((e) -> {
            if (fxthread) {
                Platform.runLater(() -> action.onAction(lastWindow, actionId));
            } else {
                action.onAction(lastWindow, actionId);
            }
        });

        ctx.add(item);

        return allowSubs ? new MainContextEntry(this, action, (JMenu) item) : null;
    }

    @Override
    public void addSeperator() {
        ctx.addSeparator();
    }

    @Override
    public void show(IGUIWindow window, int x, int y) {
        lastWindow = window;
        Component invoker = null;
        if (window != null) {

            IWindow w = window.asWindow();
            if (w != null && w instanceof MainWindow mw) {
                invoker = mw.getHandler().getFrame();
            }

        }
        ctx.show(invoker, x, y);
    }
}
