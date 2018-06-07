package to.guest;

/**
 * Created by arkady on 19/06/16.
 */
public class GuestReservationTicket {
	private String venueName;
	private String eventName;
	private String eventStartsAt;
	private String date;
	private String guestUserpic;
	private String guestName;
	private String bookedBy;
	private String qrTicket;
	private String rawDate;
	private Boolean hasBottleService;
	private Short guestsNumber;
	private Long guestId;
	private Integer minSpend;
	private Short bottleMin;
	private String eventImage;

	public GuestReservationTicket() {
	}

	public String getRawDate() {
		return rawDate;
	}

	public void setRawDate(String rawDate) {
		this.rawDate = rawDate;
	}

	public String getVenueName() {
		return venueName;
	}

	public void setVenueName(String venueName) {
		this.venueName = venueName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getGuestUserpic() {
		return guestUserpic;
	}

	public void setGuestUserpic(String guestUserpic) {
		this.guestUserpic = guestUserpic;
	}

	public String getGuestName() {
		return guestName;
	}

	public void setGuestName(String guestName) {
		this.guestName = guestName;
	}

	public String getBookedBy() {
		return bookedBy;
	}

	public void setBookedBy(String bookedBy) {
		this.bookedBy = bookedBy;
	}

	public String getQrTicket() {
		return qrTicket;
	}

	public void setQrTicket(String qrTicket) {
		this.qrTicket = qrTicket;
	}

	public Boolean getHasBottleService() {
		return hasBottleService;
	}

	public void setHasBottleService(Boolean hasBottleService) {
		this.hasBottleService = hasBottleService;
	}

	public Short getGuestsNumber() {
		return guestsNumber;
	}

	public void setGuestsNumber(Short guestsNumber) {
		this.guestsNumber = guestsNumber;
	}

	public Long getGuestId() {
		return guestId;
	}

	public void setGuestId(Long guestId) {
		this.guestId = guestId;
	}

	public Integer getMinSpend() {
		return minSpend;
	}

	public void setMinSpend(Integer minSpend) {
		this.minSpend = minSpend;
	}

	public Short getBottleMin() {
		return bottleMin;
	}

	public void setBottleMin(Short bottleMin) {
		this.bottleMin = bottleMin;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventStartsAt() {
		return eventStartsAt;
	}

	public void setEventStartsAt(String eventStartsAt) {
		this.eventStartsAt = eventStartsAt;
	}

	public String getEventImage() {
		return eventImage;
	}

	public void setEventImage(String eventImage) {
		this.eventImage = eventImage;
	}
}
