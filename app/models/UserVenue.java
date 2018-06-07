package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.DbJsonB;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModelProperty;
import play.libs.Json;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by arkady on 16/02/16.
 */
@Entity
@Table(name = "user_venue")
public class UserVenue extends Model {

	@EmbeddedId
	private UserVenuePK pk = new UserVenuePK();

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "venue_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
	private Venue venue;

	@Enumerated(EnumType.STRING)
	@Column(name = "request_status")
	private VenueRequestStatus requestStatus;

	@ApiModelProperty(example = "2016-07-09", readOnly = true)
	private LocalDate since;

	@DbJsonB
	private JsonNode roles;

	public static Finder<UserVenuePK, UserVenue> finder = new Finder<>(UserVenue.class);

	public UserVenuePK getPk() {
		return pk;
	}

	public void setPk(UserVenuePK pk) {
		this.pk = pk;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.getPk().userId = user.getId();
		this.user = user;
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.getPk().venueId = venue.getId();
		this.venue = venue;
	}

	public VenueRequestStatus getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(VenueRequestStatus requestStatus) {
		this.requestStatus = requestStatus;
	}

	public List<VenueRole> getRoles() {
		if (roles != null) {
			final List<VenueRole> strings = Arrays.asList(Json.fromJson(roles, VenueRole[].class));
			return new ArrayList<>(strings);
		} else {
			return new ArrayList<>();
		}

	}

	public void setRoles(List<VenueRole> roles) {
		this.roles = Json.toJson(roles);
	}

	public LocalDate getSince() {
		return since;
	}

	public void setSince(LocalDate since) {
		this.since = since;
	}
}
