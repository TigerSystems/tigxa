package de.MarkusTieger.Tigxa.api.impl.main.gui.screen;

import de.MarkusTieger.Tigxa.api.IAPI;
import de.MarkusTieger.Tigxa.api.gui.IScreen;
import de.MarkusTieger.Tigxa.api.gui.registry.IScreenRegistry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainScreenRegistry implements IScreenRegistry {

    private final IAPI api;
    private final Map<String, Map<String, IScreen>> content = Collections.synchronizedMap(new HashMap<>());

    public MainScreenRegistry(IAPI api){
        this.api = api;
    }

    @Override
    public IScreen getRegistredScreen(String namespace, String id) {
        synchronized (content){
            Map<String, IScreen> ids = content.get(namespace.toLowerCase());
            if(ids == null) return null;
            synchronized (ids){
                return ids.get(id.toLowerCase());
            }
        }
    }

    @Override
    public IScreen registerScreen(IScreen screen, String id) {
        return registerScreen(screen, api.getNamespace(), id);
    }

    public IScreen registerScreen(IScreen screen, String namespace, String id) {
        synchronized (content){
            Map<String, IScreen> ids = content.get(namespace.toLowerCase());
            if(ids == null) {
                ids = Collections.synchronizedMap(new HashMap<>());
                content.put(namespace.toLowerCase(), ids);
            }
            synchronized (ids){
                if(ids.containsKey(id.toLowerCase())){
                    ids.replace(id.toLowerCase(), screen);
                } else {
                    ids.put(id.toLowerCase(), screen);
                }
            }
        }
        return screen;
    }

    public void unregisterScreen(String namespace, String id) {
        synchronized (content){
            Map<String, IScreen> ids = content.get(namespace.toLowerCase());
            if(ids == null) {
                return;
            }
            synchronized (ids){
                ids.remove(id.toLowerCase());
            }
        }
    }

    @Override
    public void unregisterScreen(String id) {
        unregisterScreen(api.getNamespace(), id);
    }
}
