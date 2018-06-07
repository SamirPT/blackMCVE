package tech.peller.mrblackandroidwatch.models.venue;

/**
 * Created by arkady on 16/02/16.
 */
public enum VenueRequestStatus {
	APPROVED("Approved"),
	REQUESTED("Requested"),
	PREVIOUS("Previous");

	private String name;
	VenueRequestStatus(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return name;
	}

}
