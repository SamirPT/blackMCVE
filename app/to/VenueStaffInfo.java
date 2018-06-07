package to;

import io.swagger.annotations.ApiModelProperty;
import models.User;
import models.VenueRequestStatus;

/**
 * Created by arkady on 13/03/16.
 */
public class VenueStaffInfo extends UserInfo {

	@ApiModelProperty(readOnly = true, example = "2016-05-31")
	private String since;
	private VenueRequestStatus requestStatus;


	public VenueStaffInfo() {
	}

	public VenueStaffInfo(User user) {
		super(user);
	}

	public String getSince() {
		return since;
	}

	public void setSince(String since) {
		this.since = since;
	}

	public VenueRequestStatus getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(VenueRequestStatus requestStatus) {
		this.requestStatus = requestStatus;
	}
}
