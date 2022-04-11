package de.MarkusTieger.Tigxa.extension.impl.internal;

import de.MarkusTieger.Tigxa.Browser;
import de.MarkusTieger.Tigxa.extension.api.IAPI;
import de.MarkusTieger.Tigxa.extension.api.gui.IGUIWindow;
import de.MarkusTieger.Tigxa.extension.api.gui.context.IContextEntry;
import de.MarkusTieger.Tigxa.extension.api.gui.context.IContextMenu;
import de.MarkusTieger.Tigxa.extension.api.window.ITab;
import de.MarkusTieger.Tigxa.extension.impl.BasicExtension;
import de.MarkusTieger.Tigxa.gui.window.ConfigWindow;

public class SettingsExtension extends BasicExtension {

    private final IAPI api;

    public SettingsExtension(IAPI api) {
        super("Settings", Browser.VERSION, new String[]{"MarkusTieger"}, Browser.class.getResource("/res/gui/extensions/settings.png"));
        this.api = api;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onAction(IGUIWindow window, int relativeX, int relativeY, int absoluteX, int absoluteY) {

        IContextMenu menu = api.getGUIManager().createContextMenu(true, api.getActionHandler());

        menu.addEntry("New Tab", "new_tab", false);
        menu.addEntry("New Window", "new_window", false);

        menu.addSeperator();

        menu.addEntry("Downloads", "downloads", false);
        menu.addEntry("Passwords", "passwords", false);
        menu.addEntry("Extensions", "extensions", false);

        menu.addSeperator();

        menu.addEntry("Print", "print", false);
        // menu.addEntry("Search", "search", false);

        IContextEntry entry = menu.addEntry("Zoom", "", true);
        entry.addEntry("200%", "zoom_200", false);
        entry.addEntry("100%", "zoom_100", false);
        entry.addEntry("75%", "zoom_75", false);
        entry.addEntry("50%", "zoom_50", false);
        entry.addEntry("25%", "zoom_25", false);

        menu.addSeperator();

        menu.addEntry("Settings", "settings", false);

        entry = menu.addEntry("More Tools", "", true);
        entry.addEntry("Open Web-Terminal", "terminal", false);
        entry.addEntry("Show Source-Code", "source", false);

        entry = menu.addEntry("Help", "", true);
        entry.addEntry("Get Help", "help", false);
        entry.addEntry("Send Feedback", "feedback", false);
        entry.addEntry("Check for Updates", "update", false);
        entry.addEntry("About " + Browser.NAME, "about", false);

        menu.addSeperator();

        menu.addEntry("Exit", "exit", false);

        menu.show(window, relativeX, relativeY);

    }

    @Override
    public void onAction(IGUIWindow window, String id) {

        if (id.equalsIgnoreCase("new_tab")) {
            if (window == null) return;
            window.asWindow().add(null, true);
        }

        if (id.equalsIgnoreCase("new_window")) {
            api.getWindowManager().addWindow().add(null, true);
        }

        if (id.equalsIgnoreCase("print")) {
            window.asWindow().getSelectedTab().print();
        }

        if (id.toLowerCase().startsWith("zoom_".toLowerCase())) {
            String data = id.substring(5);
            try {
                int value = Integer.parseInt(data);
                double factor = (((double) value) / 100D);

                ITab tab = window.asWindow().getSelectedTab();
                if (tab == null) return;
                tab.setZoom(factor);
            } catch (NumberFormatException e) {
            }
        }

        if (id.equalsIgnoreCase("settings")) {
            ConfigWindow.create();
        }

        if (id.equalsIgnoreCase("exit")) {
            System.exit(0);
        }

    }
}
