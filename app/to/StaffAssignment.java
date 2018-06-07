package to;

import models.ReservationUser;
import models.VenueRole;

/**
 * Created by arkady on 17/05/16.
 */
public class StaffAssignment extends SearchUserInfo {
	private VenueRole role;
	private String userpic;

	public StaffAssignment() {
	}

	public StaffAssignment(ReservationUser assignment) {
		super(assignment.getUser());
		this.role = assignment.getPk().getRole();
		this.userpic = assignment.getUser().getUserpic();
	}

	public StaffAssignment(Long id, String name, String phone, VenueRole role) {
		super(id, name, phone);
		this.role = role;
	}

	public VenueRole getRole() {
		return role;
	}

	public void setRole(VenueRole role) {
		this.role = role;
	}

	public String getUserpic() {
		return userpic;
	}

	public void setUserpic(String userpic) {
		this.userpic = userpic;
	}
}
