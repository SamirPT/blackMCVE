package to.guest;

import to.ReservationInfo;

/**
 * Created by arkady on 19/06/16.
 */
public class GuestReservationInfo extends ReservationInfo {
	private String venueName;
	private String eventName;
	private String eventImage;
	private String eventStartsAt;

	public GuestReservationInfo() {
		super();
	}

	public GuestReservationInfo(ReservationInfo reservationInfo) {
		super();
		this.setId(reservationInfo.getId());
		this.setGuestInfo(reservationInfo.getGuestInfo());
		this.setGuestContactInfo(reservationInfo.getGuestContactInfo());
		this.setGuestsNumber(reservationInfo.getGuestsNumber());
		this.setTotalGuests(reservationInfo.getTotalGuests());
		this.setArrivedGuests(reservationInfo.getArrivedGuests());
		this.setArrivedGirls(reservationInfo.getArrivedGirls());
		this.setArrivedGuys(reservationInfo.getArrivedGuys());
		this.setNotifyMgmtOnArrival(reservationInfo.getNotifyMgmtOnArrival());
		this.setBookingNote(reservationInfo.getBookingNote());
		this.setComplimentGirls(reservationInfo.getComplimentGirls());
		this.setComplimentGuys(reservationInfo.getComplimentGuys());
		this.setComplimentGuysQty(reservationInfo.getComplimentGuysQty());
		this.setComplimentGirlsQty(reservationInfo.getComplimentGirlsQty());
		this.setComplimentGroupQty(reservationInfo.getComplimentGroupQty());
		this.setReducedGirls(reservationInfo.getReducedGirls());
		this.setReducedGuys(reservationInfo.getReducedGuys());
		this.setReducedGuysQty(reservationInfo.getReducedGuysQty());
		this.setReducedGirlsQty(reservationInfo.getReducedGirlsQty());
		this.setReducedGroupQty(reservationInfo.getReducedGroupQty());
		this.setMustEnter(reservationInfo.getMustEnter());
		this.setGroupType(reservationInfo.getGroupType());
		this.setBottleMin(reservationInfo.getBottleMin());
		this.setMinSpend(reservationInfo.getMinSpend());
		this.setStatus(reservationInfo.getStatus());
		this.setEventId(reservationInfo.getEventId());
		this.setTags(reservationInfo.getTags());
		this.setTableInfo(reservationInfo.getTableInfo());
		this.setBookedBy(reservationInfo.getBookedBy());
		this.setBottleService(reservationInfo.getBottleService());
		this.setTotalSpent(reservationInfo.getTotalSpent());
		this.setRating(reservationInfo.getRating());
		this.setVenueId(reservationInfo.getVenueId());
		this.setReservationDate(reservationInfo.getReservationDate());
		this.setFeedbackCount(reservationInfo.getFeedbackCount());
		this.setArrivalTime(reservationInfo.getArrivalTime());
		this.setEstimatedArrivalTime(reservationInfo.getEstimatedArrivalTime());
		this.setStatusChangeTime(reservationInfo.getStatusChangeTime());
		this.setDeleted(reservationInfo.getDeleted());
	}

	public String getVenueName() {
		return venueName;
	}

	public void setVenueName(String venueName) {
		this.venueName = venueName;
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
