package to.notifications;

import models.Notification;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by arkady on 30/09/16.
 */
public class NotificationTO {
	private Long id;
	private NotificationType type;
	private String date;
	private String time;
	private Long venueId;
	private Long eventId;
	private String eventDate;
	private Long reservationId;
	private String message;
	private Map<String,String> data;

	public NotificationTO() {
		this.data = new HashMap<>();
	}

	public NotificationTO(Notification notification) {
		this.data = new HashMap<>();
		this.id = notification.getId();
		this.type = notification.getType();
		this.date = notification.getDate().toString();
		this.time = notification.getTime().toString();
		this.message = notification.getType().getTemplate();
		this.venueId = notification.getVenueId();
		this.reservationId = notification.getReservationId();
		this.eventId = notification.getEventId();
		if (notification.getEventDate() != null) {
			this.eventDate = notification.getEventDate().toString();
		}

		this.data = notification.getData().getData();
	}

	public Long getVenueId() {
		return venueId;
	}

	public void setVenueId(Long venueId) {
		this.venueId = venueId;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getReservationId() {
		return reservationId;
	}

	public void setReservationId(Long reservationId) {
		this.reservationId = reservationId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}
}
