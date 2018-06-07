package to.admin;

import io.swagger.annotations.ApiModelProperty;
import models.Venue;
import models.VenueType;

import java.util.List;

/**
 * Created by arkady on 04/05/16.
 */
public class VenueTO {
	private VenueType venueType;
	private Long id;
	@ApiModelProperty(required = true)
	private String name;
	@ApiModelProperty(required = true)
	private String address;
	private String fbPlaceId;
	private String coverUrl;
	private String logoUrl;
	@ApiModelProperty(required = true)
	private Integer capacity = 0;
	@ApiModelProperty(required = true)
	private String timeZone;
	private List<String> tags;

	public VenueTO() {
	}

	public VenueTO(Long id, String name, String address, String fbPlaceId, String coverUrl, String logoUrl, Integer capacity, String timeZone, List<String> tags) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.fbPlaceId = fbPlaceId;
		this.coverUrl = coverUrl;
		this.logoUrl = logoUrl;
		this.capacity = capacity;
		this.timeZone = timeZone;
		this.tags = tags;
	}

	public VenueTO(Venue venue) {
		this.id = venue.getId();
		this.name = venue.getName();
		this.address = venue.getAddress();
		this.fbPlaceId = venue.getPlaceId();
		this.coverUrl = venue.getCoverUrl();
		this.logoUrl = venue.getLogoUrl();
		this.capacity = venue.getCapacity();
		this.timeZone = venue.getTimeZone();
		this.tags = venue.getTags();
		this.venueType = venue.getVenueType();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFbPlaceId() {
		return fbPlaceId;
	}

	public void setFbPlaceId(String fbPlaceId) {
		this.fbPlaceId = fbPlaceId;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public VenueType getVenueType() {
		return venueType;
	}

	public void setVenueType(VenueType venueType) {
		this.venueType = venueType;
	}
}
