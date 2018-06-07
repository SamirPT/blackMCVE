package models;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by arkady on 06/05/16.
 */
@Entity
@Table(name = "reservation_userr")
public class ReservationUser extends Model {

	@EmbeddedId
	private ReservationUserPK pk = new ReservationUserPK();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reservation_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
	private Reservation reservation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userr_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
	private User user;

	public static Finder<ReservationUserPK, ReservationUser> finder = new Finder<>(ReservationUser.class);

	public ReservationUserPK getPk() {
		return pk;
	}

	public void setPk(ReservationUserPK pk) {
		this.pk = pk;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
