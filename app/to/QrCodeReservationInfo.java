package to;

/**
 * Created by arkady on 21/04/16.
 */
public class QrCodeReservationInfo {
	private Long reservationId;
	private Long eventId;
	private Long venueId;

	public QrCodeReservationInfo() {
	}

	public QrCodeReservationInfo(Long reservationId, Long eventId, Long venueId) {
		this.reservationId = reservationId;
		this.eventId = eventId;
		this.venueId = venueId;
	}

	public Long getReservationId() {
		return reservationId;
	}

	public void setReservationId(Long reservationId) {
		this.reservationId = reservationId;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getVenueId() {
		return venueId;
	}

	public void setVenueId(Long venueId) {
		this.venueId = venueId;
	}
}
