package models;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created by arkady on 02/04/16.
 */
@Embeddable
public class ClickerPointPk {

	@Column(name = "venue_id", nullable = false)
	private Long venueId;

	@Column(name = "date", nullable = false)
	private LocalDate date;

	@Column(name = "time", nullable = false)
	private LocalTime time;

	public ClickerPointPk() {
	}

	public ClickerPointPk(Long venueId, LocalDate date, LocalTime time) {
		this.venueId = venueId;
		this.date = date;
		this.time = time;
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

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ClickerPointPk that = (ClickerPointPk) o;

		if (venueId != null ? !venueId.equals(that.venueId) : that.venueId != null) return false;
		if (date != null ? !date.equals(that.date) : that.date != null) return false;
		return !(time != null ? !time.equals(that.time) : that.time != null);

	}

	@Override
	public int hashCode() {
		int result = venueId != null ? venueId.hashCode() : 0;
		result = 31 * result + (date != null ? date.hashCode() : 0);
		result = 31 * result + (time != null ? time.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "ClickerPointPk{" +
				"venueId=" + venueId +
				", date=" + date +
				", time=" + time +
				'}';
	}
}
