package to;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by arkady on 26/03/16.
 */
public class FeedbackReservationInfo {
	@ApiModelProperty(readOnly = true)
	private Long reservationId;
	@ApiModelProperty(required = true, example = "2016-10-02")
	private String reservationDate;

	public FeedbackReservationInfo() {
	}

	public FeedbackReservationInfo(Long reservationId, String reservationDate) {
		this.reservationId = reservationId;
		this.reservationDate = reservationDate;
	}

	public Long getReservationId() {
		return reservationId;
	}

	public void setReservationId(Long reservationId) {
		this.reservationId = reservationId;
	}

	public String getReservationDate() {
		return reservationDate;
	}

	public void setReservationDate(String reservationDate) {
		this.reservationDate = reservationDate;
	}
}
