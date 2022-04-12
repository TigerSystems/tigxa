package de.MarkusTieger.Tigxa.api.impl.extension.events;

import de.MarkusTieger.Tigxa.api.event.IEvent;
import de.MarkusTieger.Tigxa.api.window.ITab;
import de.MarkusTieger.Tigxa.api.window.IWindow;
import lombok.Getter;

@Getter
public class PopupCreationEvent implements IEvent {

    public static final String NAME = "popup_creation";

    private final IWindow window;
    private final ITab tab;

    public PopupCreationEvent(IWindow window, ITab tab){
        this.window = window;
        this.tab = tab;
        // TODO: Event-Permission-System
    }

    @Override
    public String getName() {
        return NAME;
    }
}
