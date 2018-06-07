package to;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkady on 29/03/16.
 */
public class EventsList {
	private List<EventInfo> eventsList = new ArrayList<>();

	public EventsList() {
	}

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
