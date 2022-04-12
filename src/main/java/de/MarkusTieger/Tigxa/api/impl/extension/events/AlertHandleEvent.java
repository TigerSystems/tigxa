package de.MarkusTieger.Tigxa.api.impl.extension.events;

import de.MarkusTieger.Tigxa.api.event.IEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlertHandleEvent implements IEvent {

    private static final String NAME = "alert";

    private final boolean canceled;
    private final String data;

    public AlertHandleEvent(String data, boolean canceled){
        this.data = data;
        this.canceled = canceled;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
