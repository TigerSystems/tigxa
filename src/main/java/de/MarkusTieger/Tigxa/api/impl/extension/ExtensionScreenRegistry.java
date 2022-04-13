package de.MarkusTieger.Tigxa.api.impl.extension;

import de.MarkusTieger.Tigxa.api.IAPI;
import de.MarkusTieger.Tigxa.api.gui.IScreen;
import de.MarkusTieger.Tigxa.api.gui.registry.IScreenRegistry;
import de.MarkusTieger.Tigxa.api.impl.main.gui.screen.MainScreenRegistry;

public class ExtensionScreenRegistry implements IScreenRegistry {

    private final IAPI api;
    private final MainScreenRegistry registry;

    public ExtensionScreenRegistry(IAPI api, MainScreenRegistry parent) {
        this.api = api;
        registry = parent;
    }

    @Override
    public IScreen getRegistredScreen(String s, String s1) {
        return registry.getRegistredScreen(s, s1);
    }

    @Override
    public IScreen registerScreen(IScreen iScreen, String s) {
        return registry.registerScreen(iScreen, api.getNamespace(), s);
    }

    @Override
    public void unregisterScreen(String s) {
        registry.unregisterScreen(api.getNamespace(), s);
    }
}
