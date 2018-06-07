package to;

import io.swagger.annotations.ApiModelProperty;
import to.venue.EODReservationInfo;

import java.util.List;

/**
 * Created by arkady on 02/08/16.
 */
public class CustomerInfo {
	@ApiModelProperty(readOnly = true)
	private VisitorInfo visitorInfo;

	@ApiModelProperty(readOnly = true)
	private UserInfo firstBookedBy;

	@ApiModelProperty(readOnly = true)
	private List<EODReservationInfo> pastReservations;
	@ApiModelProperty(readOnly = true)
	private List<EODReservationInfo> comingUpReservations;

	@ApiModelProperty(readOnly = true)
	private List<FeedbackInfo> notes;

	private String customerNote;
	private List<String> tags;
	private PayInfoTO contactInfo;

	public VisitorInfo getVisitorInfo() {
		return visitorInfo;
	}

	public void setVisitorInfo(VisitorInfo visitorInfo) {
		this.visitorInfo = visitorInfo;
	}

	public String getCustomerNote() {
		return customerNote;
	}

	public void setCustomerNote(String customerNote) {
		this.customerNote = customerNote;
	}

	public UserInfo getFirstBookedBy() {
		return firstBookedBy;
	}

	public void setFirstBookedBy(UserInfo firstBookedBy) {
		this.firstBookedBy = firstBookedBy;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<EODReservationInfo> getPastReservations() {
		return pastReservations;
	}

	public void setPastReservations(List<EODReservationInfo> pastReservations) {
		this.pastReservations = pastReservations;
	}

	public List<EODReservationInfo> getComingUpReservations() {
		return comingUpReservations;
	}

	public void setComingUpReservations(List<EODReservationInfo> comingUpReservations) {
		this.comingUpReservations = comingUpReservations;
	}

	public List<FeedbackInfo> getNotes() {
		return notes;
	}

	public void setNotes(List<FeedbackInfo> notes) {
		this.notes = notes;
	}

	public PayInfoTO getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(PayInfoTO contactInfo) {
		this.contactInfo = contactInfo;
	}
}
