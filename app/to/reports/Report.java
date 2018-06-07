package to.reports;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkady on 01/04/16.
 */
public class Report {
	private List<? extends ReservationItem> reservationItems = new ArrayList<>();

	public Report() {
	}

	public Report(List<? extends ReservationItem> reservationItems) {
		this.reservationItems = reservationItems;
	}

	public List<? extends ReservationItem> getReservationItems() {
		return reservationItems;
	}

	public void setReservationItems(List<? extends ReservationItem> reservationItems) {
		this.reservationItems = reservationItems;
	}
}
