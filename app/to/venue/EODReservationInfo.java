package to.venue;

import to.*;

/**
 * Created by arkady on 20/07/16.
 */
public class EODReservationInfo extends ReservationInfo {

	private StaffAssignment completedBy;
	private StaffAssignment confirmedBy;

	public StaffAssignment getCompletedBy() {
		return completedBy;
	}

	public void setCompletedBy(StaffAssignment completedBy) {
		this.completedBy = completedBy;
	}

	public StaffAssignment getConfirmedBy() {
		return confirmedBy;
	}

	public void setConfirmedBy(StaffAssignment confirmedBy) {
		this.confirmedBy = confirmedBy;
	}
}
