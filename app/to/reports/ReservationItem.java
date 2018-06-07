package to.reports;

import models.BottleServiceType;

/**
 * Created by arkady on 01/04/16.
 */
public class ReservationItem {
	private String userpic;
	private String fullName;
	private String reservedBy;
	private Long reservedById;
	private String arrived;
	private String date;
	private Short rating;
	private Integer minSpend;
	private Integer actualSpent;
	private Integer guestsBooked;
	private Short guestsActual;
	private BottleServiceType bottleServiceType;
	private Boolean isActualReservation;
	private Short guysSeated;
	private Short girlsSeated;

	public ReservationItem() {
	}

	public ReservationItem(String date, String userpic, String fullName, String reservedBy, String arrived, Short rating, Integer minSpend, Integer actualSpent) {
		this.date = date;
		this.userpic = userpic;
		this.fullName = fullName;
		this.reservedBy = reservedBy;
		this.arrived = arrived;
		this.rating = rating;
		this.minSpend = minSpend;
		this.actualSpent = actualSpent;
	}

	public BottleServiceType getBottleServiceType() {
		return bottleServiceType;
	}

	public void setBottleServiceType(BottleServiceType bottleServiceType) {
		this.bottleServiceType = bottleServiceType;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getReservedBy() {
		return reservedBy;
	}

	public void setReservedBy(String reservedBy) {
		this.reservedBy = reservedBy;
	}

	public String getArrived() {
		return arrived;
	}

	public void setArrived(String arrived) {
		this.arrived = arrived;
	}

	public Short getRating() {
		return rating;
	}

	public void setRating(Short rating) {
		this.rating = rating;
	}

	public Integer getMinSpend() {
		return minSpend;
	}

	public void setMinSpend(Integer minSpend) {
		this.minSpend = minSpend;
	}

	public Integer getActualSpent() {
		return actualSpent;
	}

	public void setActualSpent(Integer actualSpent) {
		this.actualSpent = actualSpent;
	}

	public String getUserpic() {
		return userpic;
	}

	public void setUserpic(String userpic) {
		this.userpic = userpic;
	}

	public Long getReservedById() {
		return reservedById;
	}

	public void setReservedById(Long reservedById) {
		this.reservedById = reservedById;
	}

	public Integer getGuestsBooked() {
		return guestsBooked;
	}

	public void setGuestsBooked(Integer guestsBooked) {
		this.guestsBooked = guestsBooked;
	}

	public Short getGuestsActual() {
		return guestsActual;
	}

	public void setGuestsActual(Short guestsActual) {
		this.guestsActual = guestsActual;
	}

	public Boolean getIsActualReservation() {
		return isActualReservation;
	}

	public void setIsActualReservation(Boolean actualReservation) {
		isActualReservation = actualReservation;
	}

	public Short getGuysSeated() {
		return guysSeated;
	}

	public void setGuysSeated(Short guysSeated) {
		this.guysSeated = guysSeated;
	}

	public Short getGirlsSeated() {
		return girlsSeated;
	}

	public void setGirlsSeated(Short girlsSeated) {
		this.girlsSeated = girlsSeated;
	}
}
