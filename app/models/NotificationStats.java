package models;

import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * Created by arkady on 30/09/16.
 */
@Entity
public class NotificationStats extends Model {

	@EmbeddedId
	private UserVenuePK pk = new UserVenuePK();

	@Column(columnDefinition = "integer default 0")
	private int unreadReservationsApprovalRequest;

	@Column(columnDefinition = "integer default 0")
	private int unreadReservationsApproved;

	@Column(columnDefinition = "integer default 0")
	private int unreadReservationsRejected;

	@Column(columnDefinition = "integer default 0")
	private int unreadTableReleased;

	@Column(columnDefinition = "integer default 0")
	private int unreadEmployees;

	@Column(columnDefinition = "integer default 0")
	private int unreadAssignments;

	@Column(columnDefinition = "integer default 0")
	private int notifyMeReservationArrival;

	public static Finder<UserVenuePK, NotificationStats> finder = new Finder<>(NotificationStats.class);

	public NotificationStats() {
	}

	public NotificationStats(UserVenuePK pk) {
		this.pk = pk;
	}

	public UserVenuePK getPk() {
		return pk;
	}

	public void setPk(UserVenuePK pk) {
		this.pk = pk;
	}

	public int getUnreadReservationsApprovalRequest() {
		return unreadReservationsApprovalRequest;
	}

	public void setUnreadReservationsApprovalRequest(int unreadReservationsApprovalRequest) {
		this.unreadReservationsApprovalRequest = unreadReservationsApprovalRequest;
	}

	public int getUnreadReservationsApproved() {
		return unreadReservationsApproved;
	}

	public void setUnreadReservationsApproved(int unreadReservationsApproved) {
		this.unreadReservationsApproved = unreadReservationsApproved;
	}

	public int getUnreadReservationsRejected() {
		return unreadReservationsRejected;
	}

	public void setUnreadReservationsRejected(int unreadReservationsRejected) {
		this.unreadReservationsRejected = unreadReservationsRejected;
	}

	public int getUnreadTableReleased() {
		return unreadTableReleased;
	}

	public void setUnreadTableReleased(int unreadTableReleased) {
		this.unreadTableReleased = unreadTableReleased;
	}

	public int getUnreadEmployees() {
		return unreadEmployees;
	}

	public void setUnreadEmployees(int unreadEmployees) {
		this.unreadEmployees = unreadEmployees;
	}

	public int getUnreadAssignments() {
		return unreadAssignments;
	}

	public void setUnreadAssignments(int unreadAssignments) {
		this.unreadAssignments = unreadAssignments;
	}

	public int getNotifyMeReservationArrival() {
		return notifyMeReservationArrival;
	}

	public void setNotifyMeReservationArrival(int notifyMeReservationArrival) {
		this.notifyMeReservationArrival = notifyMeReservationArrival;
	}
}
