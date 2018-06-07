package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.DbJsonB;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by arkady on 12/03/16.
 */
@Entity
public class Feedback extends Model {

	@Id
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id", referencedColumnName = "id", nullable = false)
	private User author;

	@Enumerated(EnumType.STRING)
	private VenueRole authorRole;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reservation_id", referencedColumnName = "id", nullable = false)
	private Reservation reservation;

	@Column(columnDefinition = "varchar(3000)")
	private String message;
	private String staffAssignment;
	private Short stars;

	@Column(name = "ttime", nullable = false)
	private LocalTime time;

	@Column(name = "meta", columnDefinition = "boolean default false")
	private boolean meta;

	@DbJsonB
	private JsonNode tags;

	public static Finder<Long, Feedback> find = new Finder<>(Feedback.class);

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public short getStars() {
		return stars;
	}

	public void setStars(short stars) {
		this.stars = stars;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getStaffAssignment() {
		return staffAssignment;
	}

	public void setStaffAssignment(String staffAssignment) {
		this.staffAssignment = staffAssignment;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public VenueRole getAuthorRole() {
		return authorRole;
	}

	public void setAuthorRole(VenueRole authorRole) {
		this.authorRole = authorRole;
	}

	public List<String> getTags() {
		if (tags != null) {
			final List<String> tagList = Arrays.asList(Json.fromJson(tags, String[].class));
			return new ArrayList<>(tagList);
		} else {
			return new ArrayList<>();
		}
	}

	public boolean isMeta() {
		return meta;
	}

	public void setMeta(boolean meta) {
		this.meta = meta;
	}

	public void setTags(List<String> tags) {
		this.tags = Json.toJson(tags);
	}
}
