package to.guest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkady on 19/06/16.
 */
public class GuestReservations {
	private List<GuestReservationInfo> past = new ArrayList<>();
	private List<GuestReservationInfo> comingUp = new ArrayList<>();

	public List<GuestReservationInfo> getPast() {
		return past;
	}

	public void setPast(List<GuestReservationInfo> past) {
		this.past = past;
	}

	public List<GuestReservationInfo> getComingUp() {
		return comingUp;
	}

	public void setComingUp(List<GuestReservationInfo> comingUp) {
		this.comingUp = comingUp;
	}
}
