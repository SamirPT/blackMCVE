package tech.peller.mrblackandroidwatch.enums;

/**
 * Created by arkady on 16/02/16.
 */
public enum VenueRequestStatusEnum {
	APPROVED("Approved"),
	REQUESTED("Requested"),
	PREVIOUS("Previous");

	private String name;
	VenueRequestStatusEnum(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return name;
	}

}
