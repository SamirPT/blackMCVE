package to;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkady on 01/03/16.
 */
public class VenuePeopleList {
	private List<VenueStaffInfo> peoplesRequests = new ArrayList<>();
	private List<VenueStaffInfo> approvedPeoples = new ArrayList<>();
	private List<PromoterRequest> promotersRequests = new ArrayList<>();
	private List<PromoterRequest> approvedPromoters = new ArrayList<>();

	public List<VenueStaffInfo> getPeoplesRequests() {
		return peoplesRequests;
	}

	public void setPeoplesRequests(List<VenueStaffInfo> peoplesRequests) {
		this.peoplesRequests = peoplesRequests;
	}

	public List<VenueStaffInfo> getApprovedPeoples() {
		return approvedPeoples;
	}

	public void setApprovedPeoples(List<VenueStaffInfo> approvedPeoples) {
		this.approvedPeoples = approvedPeoples;
	}

	public List<PromoterRequest> getPromotersRequests() {
		return promotersRequests;
	}

	public void setPromotersRequests(List<PromoterRequest> promotersRequests) {
		this.promotersRequests = promotersRequests;
	}

	public List<PromoterRequest> getApprovedPromoters() {
		return approvedPromoters;
	}

	public void setApprovedPromoters(List<PromoterRequest> approvedPromoters) {
		this.approvedPromoters = approvedPromoters;
	}
}
