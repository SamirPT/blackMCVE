package to;

/**
 * Created by arkady on 03/04/16.
 */
public class SearchReservationInfo {
	private Long id;
	private String guestName;
	private Short guestsNumber;

	public SearchReservationInfo() {
	}

	public SearchReservationInfo(Long id, String guestName, Short guestsNumber) {
		this.id = id;
		this.guestName = guestName;
		this.guestsNumber = guestsNumber;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGuestName() {
		return guestName;
	}

	public void setGuestName(String guestName) {
		this.guestName = guestName;
	}

	public Short getGuestsNumber() {
		return guestsNumber;
	}

	public void setGuestsNumber(Short guestsNumber) {
		this.guestsNumber = guestsNumber;
	}
}
