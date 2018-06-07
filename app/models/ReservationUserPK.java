package models;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Created by arkady on 06/05/16.
 */
@Embeddable
public class ReservationUserPK {
	@Column(name = "reservation_id")
	private Long reservationId;
	@Column(name = "userr_id")
	private Long userId;
	@Enumerated(EnumType.STRING)
	private VenueRole role;

	public ReservationUserPK() {
	}

	public ReservationUserPK(Long reservationId, Long userId, VenueRole role) {
		this.reservationId = reservationId;
		this.userId = userId;
		this.role = role;
	}

	public Long getReservationId() {
		return reservationId;
	}

	public void setReservationId(Long reservationId) {
		this.reservationId = reservationId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public VenueRole getRole() {
		return role;
	}

	public void setRole(VenueRole role) {
		this.role = role;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ReservationUserPK that = (ReservationUserPK) o;

		if (reservationId != null ? !reservationId.equals(that.reservationId) : that.reservationId != null)
			return false;
		if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
		return role == that.role;

	}

	@Override
	public int hashCode() {
		int result = reservationId != null ? reservationId.hashCode() : 0;
		result = 31 * result + (userId != null ? userId.hashCode() : 0);
		result = 31 * result + (role != null ? role.hashCode() : 0);
		return result;
	}
}
