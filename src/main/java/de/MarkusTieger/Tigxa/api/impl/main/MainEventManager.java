package de.MarkusTieger.Tigxa.api.impl.main;

import de.MarkusTieger.Tigxa.api.event.IEvent;
import de.MarkusTieger.Tigxa.api.event.IEventHandler;
import de.MarkusTieger.Tigxa.api.event.IEventManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainEventManager implements IEventManager {

    private final List<IEventHandler> listeners = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void registerListener(IEventHandler iEventHandler) {
        synchronized (listeners){
            listeners.add(iEventHandler);
        }
    }

    @Override
    public void unregisterListener(IEventHandler iEventHandler) {
        synchronized (listeners){
            listeners.add(iEventHandler);
        }
    }

    @Override
    public IEvent call(IEvent iEvent) {
        synchronized (listeners){
            for(IEventHandler listener : listeners){
                listener.onEvent(iEvent);
            }
        }
        return iEvent;
    }

    @Override
    public IEventHandler[] getListeners() {
        synchronized (listeners){
            return listeners.toArray(new IEventHandler[0]);
        }
    }
}
