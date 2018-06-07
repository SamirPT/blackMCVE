package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.DbJsonB;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import to.notifications.NotificationType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created by arkady on 30/09/16.
 */
@Entity
public class Notification extends Model {

	@Id
	private Long id;

	@Enumerated(EnumType.STRING)
	private NotificationType type;

	private LocalDate date;
	private LocalTime time;

	private Long venueId;
	private Long eventId;
	private LocalDate eventDate;
	private Long userId;
	private Long reservationId;

	private String message;

	@DbJsonB
	private JsonNode data;

	public static Finder<Long, Notification> find = new Finder<>(Notification.class);

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getReservationId() {
		return reservationId;
	}

	public void setReservationId(Long reservationId) {
		this.reservationId = reservationId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public NotificationData getData() {
		return data != null ? Json.fromJson(data, NotificationData.class) : null;
	}

	public void setData(NotificationData data) {
		this.data = Json.toJson(data);
	}

	public Long getVenueId() {
		return venueId;
	}

	public void setVenueId(Long venueId) {
		this.venueId = venueId;
	}

	public LocalDate getEventDate() {
		return eventDate;
	}

	public void setEventDate(LocalDate eventDate) {
		this.eventDate = eventDate;
	}
}
