package to;

import java.time.LocalDate;
import java.util.Map;

/**
 * Created by arkady on 30/03/16.
 */
public class EventsOfDate {
	private String date;
	private Integer numberOfEvents;

	public EventsOfDate() {
	}

	public EventsOfDate(String date, Integer numberOfEvents) {
		this.date = date;
		this.numberOfEvents = numberOfEvents;
	}

	public EventsOfDate(Map.Entry<LocalDate, Integer> entry) {
		this.date = entry.getKey().toString();
		this.numberOfEvents = entry.getValue();
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Integer getNumberOfEvents() {
		return numberOfEvents;
	}

	public void setNumberOfEvents(Integer numberOfEvents) {
		this.numberOfEvents = numberOfEvents;
	}
}
