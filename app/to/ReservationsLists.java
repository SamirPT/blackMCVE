package to;

import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkady on 08/03/16.
 */
public class ReservationsLists {

	@ApiModelProperty(required = false, readOnly = true)
	private List<ReservationInfo> guestlist = new ArrayList<>();
	@ApiModelProperty(required = false, readOnly = true)
	private List<ReservationInfo> pending = new ArrayList<>();
	@ApiModelProperty(required = false, readOnly = true)
	private List<ReservationInfo> approved = new ArrayList<>();
	@ApiModelProperty(required = false, readOnly = true)
	private List<ReservationInfo> arrived = new ArrayList<>();

	public List<ReservationInfo> getGuestlist() {
		return guestlist;
	}

	public void setGuestlist(List<ReservationInfo> guestlist) {
		this.guestlist = guestlist;
	}

	public List<ReservationInfo> getPending() {
		return pending;
	}

	public void setPending(List<ReservationInfo> pending) {
		this.pending = pending;
	}

	public List<ReservationInfo> getApproved() {
		return approved;
	}

	public void setApproved(List<ReservationInfo> approved) {
		this.approved = approved;
	}

	public List<ReservationInfo> getArrived() {
		return arrived;
	}

	public void setArrived(List<ReservationInfo> arrived) {
		this.arrived = arrived;
	}
}
