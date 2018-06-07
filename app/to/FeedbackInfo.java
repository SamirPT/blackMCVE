package to;


import io.swagger.annotations.ApiModelProperty;
import models.Feedback;
import models.Reservation;
import models.VenueRole;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by arkady on 24/03/16.
 */
public class FeedbackInfo {

	@ApiModelProperty(readOnly = true)
	private Long id;
	@ApiModelProperty(readOnly = true)
	private UserInfo author;
	@ApiModelProperty(readOnly = true)
	private FeedbackReservationInfo shortReservationInfo;
	@ApiModelProperty(readOnly = true, example = "21:03")
	private String time;
	@ApiModelProperty(example = "Comma separated staff names")
	private String staffAssignment;
	private String message;
	private Short rating;
	private VenueRole authorRole;
	private List<String> tags;

	public FeedbackInfo() {
	}

	public FeedbackInfo(Feedback feedback) {
		if (feedback == null) return;
		this.author = new UserInfo(feedback.getAuthor());
		this.id = feedback.getId();
		this.message = feedback.getMessage();
		this.rating = feedback.getStars();
		this.authorRole = feedback.getAuthorRole();
		this.tags = feedback.getTags();

		final Reservation reservation = feedback.getReservation();
		this.shortReservationInfo = new FeedbackReservationInfo(reservation.getId(),
				reservation.getEventInstance().getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		this.staffAssignment = feedback.getStaffAssignment();
		this.time = feedback.getTime().toString();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserInfo getAuthor() {
		return author;
	}

	public void setAuthor(UserInfo author) {
		this.author = author;
	}

	public FeedbackReservationInfo getShortReservationInfo() {
		return shortReservationInfo;
	}

	public void setShortReservationInfo(FeedbackReservationInfo shortReservationInfo) {
		this.shortReservationInfo = shortReservationInfo;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getStaffAssignment() {
		return staffAssignment;
	}

	public void setStaffAssignment(String staffAssignment) {
		this.staffAssignment = staffAssignment;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Short getRating() {
		return rating;
	}

	public void setRating(Short rating) {
		this.rating = rating;
	}

	public VenueRole getAuthorRole() {
		return authorRole;
	}

	public void setAuthorRole(VenueRole authorRole) {
		this.authorRole = authorRole;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}
}