package to;

import io.swagger.annotations.ApiModelProperty;
import models.User;

/**
 * Created by arkady on 13/03/16.
 */
public class VisitorInfo extends UserInfo {
	@ApiModelProperty(readOnly = true)
	private short rating;

	@ApiModelProperty(readOnly = true)
	private int totalVisits;

	@ApiModelProperty(readOnly = true)
	private int bsVisits;

	@ApiModelProperty(readOnly = true)
	private int glVisits;

	@ApiModelProperty(readOnly = true)
	private int totalReservations;

	@ApiModelProperty(readOnly = true)
	private int glReservations;

	@ApiModelProperty(readOnly = true)
	private int feedbackCount;

	@ApiModelProperty(readOnly = true)
	private int bsReservations;

	@ApiModelProperty(readOnly = true)
	private int totalSpent;

	@ApiModelProperty(readOnly = true)
	private int avgSpent;

	@ApiModelProperty(readOnly = true)
	private String lastReservationDate;

	@ApiModelProperty(readOnly = true)
	private String lastBSReservationDate;

	@ApiModelProperty(readOnly = true)
	private String lastGLReservationDate;

	public VisitorInfo() {
	}

	public VisitorInfo(User user) {
		super(user);
	}

	public short getRating() {
		return rating;
	}

	public void setRating(short rating) {
		this.rating = rating;
	}

	public int getTotalVisits() {
		return totalVisits;
	}

	public void setTotalVisits(int totalVisits) {
		this.totalVisits = totalVisits;
	}

	public int getTotalReservations() {
		return totalReservations;
	}

	public void setTotalReservations(int totalReservations) {
		this.totalReservations = totalReservations;
	}

	public int getFeedbackCount() {
		return feedbackCount;
	}

	public void setFeedbackCount(int feedbackCount) {
		this.feedbackCount = feedbackCount;
	}

	public int getBsReservations() {
		return bsReservations;
	}

	public void setBsReservations(int bsReservations) {
		this.bsReservations = bsReservations;
	}

	public String getLastReservationDate() {
		return lastReservationDate;
	}

	public void setLastReservationDate(String lastReservationDate) {
		this.lastReservationDate = lastReservationDate;
	}

	public String getLastBSReservationDate() {
		return lastBSReservationDate;
	}

	public void setLastBSReservationDate(String lastBSReservationDate) {
		this.lastBSReservationDate = lastBSReservationDate;
	}

	public String getLastGLReservationDate() {
		return lastGLReservationDate;
	}

	public void setLastGLReservationDate(String lastGLReservationDate) {
		this.lastGLReservationDate = lastGLReservationDate;
	}

	public int getTotalSpent() {
		return totalSpent;
	}

	public void setTotalSpent(int totalSpent) {
		this.totalSpent = totalSpent;
	}

	public int getAvgSpent() {
		return avgSpent;
	}

	public void setAvgSpent(int avgSpent) {
		this.avgSpent = avgSpent;
	}

	public int getBsVisits() {
		return bsVisits;
	}

	public void setBsVisits(int bsVisits) {
		this.bsVisits = bsVisits;
	}

	public int getGlVisits() {
		return glVisits;
	}

	public void setGlVisits(int glVisits) {
		this.glVisits = glVisits;
	}

	public int getGlReservations() {
		return glReservations;
	}

	public void setGlReservations(int glReservations) {
		this.glReservations = glReservations;
	}
}
