package models;

import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created by arkady on 09/03/16.
 */
@Entity
@Table(name = "event")
public class Event extends Model {

	@Id
	private Long id;
	private String name;
	private String description;
	private String fbEventUrl;
	@Column(columnDefinition = "varchar(500)")
	private String pictureUrl;
	@Column(nullable = false)
	private LocalTime startsAt;
	@Column(nullable = false)
	private LocalTime endsAt;
	@Column(nullable = false)
	private LocalDate date;
	@Column(nullable = false)
	private Long venueId;
	private Boolean repeatable;

	@Column(columnDefinition = "boolean default false")
	private boolean deleted;
	private LocalDate deletedAt;

	public static Finder<Long, Event> find = new Finder<>(Event.class);

	public Event() {
	}

	public Event(Long id) {
		this.id = id;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFbEventUrl() {
		return fbEventUrl;
	}

	public void setFbEventUrl(String fbEventUrl) {
		this.fbEventUrl = fbEventUrl;
	}

	public LocalTime getStartsAt() {
		return startsAt;
	}

	public void setStartsAt(LocalTime startsAt) {
		this.startsAt = startsAt;
	}

	public LocalTime getEndsAt() {
		return endsAt;
	}

	public void setEndsAt(LocalTime endsAt) {
		this.endsAt = endsAt;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Long getVenueId() {
		return venueId;
	}

	public void setVenueId(Long venueId) {
		this.venueId = venueId;
	}

	public Boolean getRepeatable() {
		return repeatable;
	}

	public void setRepeatable(Boolean repeatable) {
		this.repeatable = repeatable;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public LocalDate getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(LocalDate deletedAt) {
		this.deletedAt = deletedAt;
	}
}