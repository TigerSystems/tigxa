package de.MarkusTieger.Tigxa.api.impl.extension.events;

import de.MarkusTieger.Tigxa.api.event.IEvent;
import javafx.concurrent.Worker;
import lombok.Getter;

@Getter
public class WebStateChangedEvent implements IEvent {

    private static final String NAME = "web_loading";

    private final Worker.State oldState, newState;

    public WebStateChangedEvent(Worker.State oldState, Worker.State newState){
        this.oldState = oldState;
        this.newState = newState;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
