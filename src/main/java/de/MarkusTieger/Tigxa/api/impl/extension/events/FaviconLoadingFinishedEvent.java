package de.MarkusTieger.Tigxa.api.impl.extension.events;

import de.MarkusTieger.Tigxa.api.event.IEvent;
import lombok.Getter;

import java.net.URI;

@Getter
public class FaviconLoadingFinishedEvent implements IEvent {

    private static final String NAME = "fav_loaded";

    private final String location;
    private final URI locationAsURI;
    private final boolean success;

    public FaviconLoadingFinishedEvent(String loc, URI locAsURI, boolean success){
        this.location = loc;
        this.locationAsURI = locAsURI;
        this.success = success;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
