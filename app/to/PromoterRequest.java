package to;

import io.swagger.annotations.ApiModelProperty;
import models.PromoterVenue;
import models.Venue;
import models.VenueRequestStatus;
import to.admin.VenueTO;

/**
 * Created by arkady on 09/08/16.
 */
public class PromoterRequest extends VenueTO {

	@ApiModelProperty(readOnly = true, example = "2016-05-31")
	private String since;

	private VenueRequestStatus requestStatus;

	public PromoterRequest() {
	}

	public PromoterRequest(Venue venue) {
		super(venue);
	}

	public PromoterRequest(PromoterVenue promoterVenue) {
		super(promoterVenue.getVenue());
		if (promoterVenue.getSince() != null) {
			this.since = promoterVenue.getSince().toString();
		}
		this.requestStatus = promoterVenue.getRequestStatus();
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
