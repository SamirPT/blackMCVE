package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.DbJsonB;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by arkady on 04/08/16.
 */
@Entity
@Table(name = "customer_venue")
public class CustomerVenue extends Model {

	@EmbeddedId
	private UserVenuePK pk;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "venue_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
	private Venue venue;

	@Column(columnDefinition = "varchar(1000)")
	private String customerNote;

	@ManyToOne(fetch = FetchType.LAZY)
	private User firstBookedBy;

	@DbJsonB
	private JsonNode tags;

	public static Model.Finder<UserVenuePK, CustomerVenue> finder = new Model.Finder<>(CustomerVenue.class);

	public CustomerVenue() {
	}

	public CustomerVenue(UserVenuePK pk) {
		this.pk = pk;
	}

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
		this.user = user;
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
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

	public String getCustomerNote() {
		return customerNote;
	}

	public void setCustomerNote(String customerNote) {
		this.customerNote = customerNote;
	}

	public User getFirstBookedBy() {
		return firstBookedBy;
	}

	public void setFirstBookedBy(User firstBookedBy) {
		this.firstBookedBy = firstBookedBy;
	}
}
