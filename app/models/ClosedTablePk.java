package models;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;

/**
 * Created by arkady on 30/06/16.
 */
@Embeddable
public class ClosedTablePk {
	@Column(name = "venue_id")
	private Long venueId;
	@Column(name = "place_id")
	private Long placeId;
	@Column(name = "date", nullable = false)
	private LocalDate date;

	public ClosedTablePk() {
	}

	public ClosedTablePk(Long venueId, Long placeId, LocalDate date) {
		this.venueId = venueId;
		this.placeId = placeId;
		this.date = date;
	}

	public Long getVenueId() {
		return venueId;
	}

	public void setVenueId(Long venueId) {
		this.venueId = venueId;
	}

	public Long getPlaceId() {
		return placeId;
	}

	public void setPlaceId(Long placeId) {
		this.placeId = placeId;
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

		ClosedTablePk that = (ClosedTablePk) o;

		if (venueId != null ? !venueId.equals(that.venueId) : that.venueId != null) return false;
		if (placeId != null ? !placeId.equals(that.placeId) : that.placeId != null) return false;
		return date != null ? date.equals(that.date) : that.date == null;

	}

	@Override
	public int hashCode() {
		int result = venueId != null ? venueId.hashCode() : 0;
		result = 31 * result + (placeId != null ? placeId.hashCode() : 0);
		result = 31 * result + (date != null ? date.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "ClosedTablePk{" +
				"venueId=" + venueId +
				", placeId=" + placeId +
				", date=" + date +
				'}';
	}
}
