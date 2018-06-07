package models;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Created by arkady on 16/02/16.
 */
@Embeddable
public class UserVenuePK {
	@Column(name = "user_id")
	Long userId;
	@Column(name = "venue_id")
	Long venueId;

	public UserVenuePK() {
	}

	public UserVenuePK(Long userId, Long venueId) {
		this.userId = userId;
		this.venueId = venueId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getVenueId() {
		return venueId;
	}

	public void setVenueId(Long venueId) {
		this.venueId = venueId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UserVenuePK that = (UserVenuePK) o;

		if (!userId.equals(that.userId)) return false;
		return venueId.equals(that.venueId);
	}

	@Override
	public int hashCode() {
		int result = userId.hashCode();
		result = 31 * result + venueId.hashCode();
		return result;
	}
}
