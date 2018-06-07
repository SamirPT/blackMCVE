package to;

import java.util.List;

/**
 * Created by arkady on 04/04/16.
 */
public class ScheduleInfo {
	private List<EventInfo> eventInfos;
	private List<ReservationInfo> reservations;
	private String date;

	public ScheduleInfo() {
	}

	public ScheduleInfo(List<ReservationInfo> reservations, List<EventInfo> eventInfos, String date) {
		this.reservations = reservations;
		this.eventInfos = eventInfos;
		this.date = date;
	}

	public List<EventInfo> getEventInfos() {
		return eventInfos;
	}

	public void setEventInfos(List<EventInfo> eventInfos) {
		this.eventInfos = eventInfos;
	}

	public List<ReservationInfo> getReservations() {
		return reservations;
	}

	public void setReservations(List<ReservationInfo> reservations) {
		this.reservations = reservations;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
