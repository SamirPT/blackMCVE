package models;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;

/**
 * Created by arkady on 11/03/16.
 */
@Embeddable
public class DateVenuePk {

	@Column(name = "venue_id")
	private Long venueId;
	@Column(name = "event_id")
	private Long eventId;
	@Column(name = "date", nullable = false)
	private LocalDate date;

	public DateVenuePk() {
	}

	public DateVenuePk(Long venueId, LocalDate date, Long eventId) {
		this.venueId = venueId;
		this.date = date;
		this.eventId = eventId;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getVenueId() {
		return venueId;
	}

	public void setVenueId(Long venueId) {
		this.venueId = venueId;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DateVenuePk that = (DateVenuePk) o;

		if (venueId != null ? !venueId.equals(that.venueId) : that.venueId != null) return false;
		if (eventId != null ? !eventId.equals(that.eventId) : that.eventId != null) return false;
		return date != null ? date.equals(that.date) : that.date == null;

	}

	@Override
	public int hashCode() {
		int result = venueId != null ? venueId.hashCode() : 0;
		result = 31 * result + (eventId != null ? eventId.hashCode() : 0);
		result = 31 * result + (date != null ? date.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "DateVenuePk{" +
				"venueId=" + venueId +
				", eventId=" + eventId +
				", date=" + date +
				'}';
	}
}
