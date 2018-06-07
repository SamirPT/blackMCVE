package tech.peller.mrblackandroidwatch.models.event;

import java.io.Serializable;
import java.util.List;


public class EventsList implements Serializable {
    private List<EventInfo> eventsList;

    public EventsList(List<EventInfo> eventsList) {
        this.eventsList = eventsList;
    }

    public List<EventInfo> getEventsList() {
        return eventsList;
    }

    public void setEventsList(List<EventInfo> eventsList) {
        this.eventsList = eventsList;
    }
}
