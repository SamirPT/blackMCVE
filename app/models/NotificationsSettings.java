package models;

import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * Created by arkady on 02/10/16.
 */
@Entity
public class NotificationsSettings extends Model {

	@EmbeddedId
	private UserVenuePK pk = new UserVenuePK();

	@Column(columnDefinition = "boolean default true")
	private boolean newApprovalRequestBs;
	@Column(columnDefinition = "boolean default true")
	private boolean newApprovalEmployee;
	@Column(columnDefinition = "boolean default true")
	private boolean notifyMeArrival;
	@Column(columnDefinition = "boolean default true")
	private boolean newAssignment;
	@Column(columnDefinition = "boolean default true")
	private boolean tableReleased;
	@Column(columnDefinition = "boolean default true")
	private boolean reservationICreatedApproved;
	@Column(columnDefinition = "boolean default true")
	private boolean reservationICreatedRejected;

	public static Model.Finder<UserVenuePK, NotificationsSettings> finder = new Model.Finder<>(NotificationsSettings.class);

	public NotificationsSettings() {
		this.newApprovalRequestBs = true;
		this.newApprovalEmployee = true;
		this.notifyMeArrival = true;
		this.newAssignment = true;
		this.tableReleased = true;
		this.reservationICreatedApproved = true;
		this.reservationICreatedRejected = true;
	}

	public NotificationsSettings(UserVenuePK pk) {
		this.pk = pk;
	}

	public UserVenuePK getPk() {
		return pk;
	}

	public void setPk(UserVenuePK pk) {
		this.pk = pk;
	}

	public boolean getNewApprovalRequestBs() {
		return newApprovalRequestBs;
	}

	public void setNewApprovalRequestBs(boolean newApprovalRequestBs) {
		this.newApprovalRequestBs = newApprovalRequestBs;
	}

	public boolean getNewApprovalEmployee() {
		return newApprovalEmployee;
	}

	public void setNewApprovalEmployee(boolean newApprovalEmployee) {
		this.newApprovalEmployee = newApprovalEmployee;
	}

	public boolean getNotifyMeArrival() {
		return notifyMeArrival;
	}

	public void setNotifyMeArrival(boolean notifyMeArrival) {
		this.notifyMeArrival = notifyMeArrival;
	}

	public boolean getNewAssignment() {
		return newAssignment;
	}

	public void setNewAssignment(boolean newAssignment) {
		this.newAssignment = newAssignment;
	}

	public boolean getTableReleased() {
		return tableReleased;
	}

	public void setTableReleased(boolean tableReleased) {
		this.tableReleased = tableReleased;
	}

	public boolean getReservationICreatedApproved() {
		return reservationICreatedApproved;
	}

	public void setReservationICreatedApproved(boolean reservationICreatedApproved) {
		this.reservationICreatedApproved = reservationICreatedApproved;
	}

	public boolean getReservationICreatedRejected() {
		return reservationICreatedRejected;
	}

	public void setReservationICreatedRejected(boolean reservationICreatedRejected) {
		this.reservationICreatedRejected = reservationICreatedRejected;
	}
}
