package de.MarkusTieger.Tigxa.api.impl.extension.events;

import de.MarkusTieger.Tigxa.api.event.IEvent;
import lombok.Getter;

@Getter
public class WebLocationChangedEvent implements IEvent {

    private static final String NAME = "web_location";

    private final String oldLocation, newLocation;

    public WebLocationChangedEvent(String oldLocation, String newLocation){
        this.oldLocation = oldLocation;
        this.newLocation = newLocation;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
