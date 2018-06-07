package to;

import models.NotificationsSettings;

/**
 * Created by arkady on 02/10/16.
 */
public class NotificationsSettingsTO {
//	bsReservationMadeWithNumber;
//	gl_reservation_made_with_number;
	private Boolean newApprovalRequestBs;
	private Boolean newApprovalEmployee;
	private Boolean notifyMeArrival;
	private Boolean newAssignment;
	private Boolean tableReleased;
	private Boolean reservationICreatedApproved;
	private Boolean reservationICreatedRejected;

	public NotificationsSettingsTO() {
	}

	public NotificationsSettingsTO(NotificationsSettings settings) {
		this.newApprovalRequestBs = settings.getNewApprovalRequestBs();
		this.newApprovalEmployee = settings.getNewApprovalEmployee();
		this.notifyMeArrival = settings.getNotifyMeArrival();
		this.newAssignment = settings.getNewAssignment();
		this.tableReleased = settings.getTableReleased();
		this.reservationICreatedApproved = settings.getReservationICreatedApproved();
		this.reservationICreatedRejected = settings.getReservationICreatedRejected();
	}

	public Boolean getNewApprovalRequestBs() {
		return newApprovalRequestBs;
	}

	public void setNewApprovalRequestBs(Boolean newApprovalRequestBs) {
		this.newApprovalRequestBs = newApprovalRequestBs;
	}

	public Boolean getNewApprovalEmployee() {
		return newApprovalEmployee;
	}

	public void setNewApprovalEmployee(Boolean newApprovalEmployee) {
		this.newApprovalEmployee = newApprovalEmployee;
	}

	public Boolean getNotifyMeArrival() {
		return notifyMeArrival;
	}

	public void setNotifyMeArrival(Boolean notifyMeArrival) {
		this.notifyMeArrival = notifyMeArrival;
	}

	public Boolean getNewAssignment() {
		return newAssignment;
	}

	public void setNewAssignment(Boolean newAssignment) {
		this.newAssignment = newAssignment;
	}

	public Boolean getTableReleased() {
		return tableReleased;
	}

	public void setTableReleased(Boolean tableReleased) {
		this.tableReleased = tableReleased;
	}

	public Boolean getReservationICreatedApproved() {
		return reservationICreatedApproved;
	}

	public void setReservationICreatedApproved(Boolean reservationICreatedApproved) {
		this.reservationICreatedApproved = reservationICreatedApproved;
	}

	public Boolean getReservationICreatedRejected() {
		return reservationICreatedRejected;
	}

	public void setReservationICreatedRejected(Boolean reservationICreatedRejected) {
		this.reservationICreatedRejected = reservationICreatedRejected;
	}
}
