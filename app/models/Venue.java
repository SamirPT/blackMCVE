package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.DbJsonB;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModelProperty;
import play.libs.Json;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by arkady on 15/02/16.
 */
@Entity
@Table(name = "venue")
public class Venue extends Model {

	public Venue() {
	}

	public Venue(Long id) {
		this.id = id;
	}

	@Id
	private Long id;

	@ApiModelProperty(required = true)
	private String name;

	@ApiModelProperty(required = true)
	private String address;

	private String placeId;

	private String coverUrl;

	private String logoUrl;

	@ApiModelProperty(required = true)
	private Integer capacity;

	@Column(columnDefinition = "varchar(32)", nullable = false)
	private String timeZone;

	@DbJsonB
	private JsonNode tags;

	@Transient
	@ApiModelProperty(readOnly = true)
	@Enumerated(EnumType.STRING)
	private VenueRequestStatus requestStatus;

	@Transient
	@ApiModelProperty(readOnly = true)
	@Enumerated(EnumType.STRING)
	private List<VenueRole> preferredRoles;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private VenueType venueType = VenueType.VENUE;

	@Transient
	private int unreadNotifications;

	public static Finder<Long, Venue> finder = new Finder<>(Venue.class);

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

	public VenueRequestStatus getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(VenueRequestStatus requestStatus) {
		this.requestStatus = requestStatus;
	}

	public List<VenueRole> getPreferredRoles() {
		return preferredRoles;
	}

	public void setPreferredRoles(List<VenueRole> preferredRoles) {
		this.preferredRoles = preferredRoles;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	public List<String> getTags() {
		if (tags != null) {
			final List<String> tagList = Arrays.asList(Json.fromJson(tags, String[].class));
			return new ArrayList<>(tagList);
		} else {
			return new ArrayList<>();
		}
	}

	public void setTags(List<String> tags) {
		this.tags = Json.toJson(tags);
	}

	public VenueType getVenueType() {
		return venueType;
	}

	public void setVenueType(VenueType venueType) {
		this.venueType = venueType;
	}

	public int getUnreadNotifications() {
		return unreadNotifications;
	}

	public void setUnreadNotifications(int unreadNotifications) {
		this.unreadNotifications = unreadNotifications;
	}
}
