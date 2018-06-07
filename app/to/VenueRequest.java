package to;

import io.swagger.annotations.ApiModelProperty;
import models.VenueRole;

import java.util.List;

/**
 * Created by arkady on 20/02/16.
 */
public class VenueRequest {

	@ApiModelProperty(required = false)
	private Long userId;

	@ApiModelProperty(required = true)
	private List<VenueRole> preferredRoles;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<VenueRole> getPreferredRoles() {
		return preferredRoles;
	}

	public void setPreferredRoles(List<VenueRole> preferredRoles) {
		this.preferredRoles = preferredRoles;
	}
}
