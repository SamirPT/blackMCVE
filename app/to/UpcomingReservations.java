package to;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by arkady on 10/08/16.
 */
public class UpcomingReservations {
	private List<ReservationInfo> reservationInfos = new ArrayList<>();
	private Set<EventInfo> eventInfos = new HashSet<>();

	public List<ReservationInfo> getReservationInfos() {
		return reservationInfos;
	}

	public void setReservationInfos(List<ReservationInfo> reservationInfos) {
		this.reservationInfos = reservationInfos;
	}

	public Set<EventInfo> getEventInfos() {
		return eventInfos;
	}

	public void setEventInfos(Set<EventInfo> eventInfos) {
		this.eventInfos = eventInfos;
	}
}
