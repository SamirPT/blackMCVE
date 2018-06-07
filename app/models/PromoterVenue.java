package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by arkady on 08/08/16.
 */
@Entity
@Table(name = "promoter_venue")
public class PromoterVenue extends Model {

	@EmbeddedId
	private PromoterVenuePK pk = new PromoterVenuePK();

	@ManyToOne
	@JoinColumn(name = "venue_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
	private Venue venue;

	@ManyToOne
	@JoinColumn(name = "promoter_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
	private Venue promoter;


	@Enumerated(EnumType.STRING)
	@Column(name = "request_status")
	private VenueRequestStatus requestStatus;

	private LocalDate since;

	public PromoterVenue() {
	}

	public PromoterVenue(PromoterVenuePK pk) {
		this.pk = pk;
	}

	public PromoterVenuePK getPk() {
		return pk;
	}

	public void setPk(PromoterVenuePK pk) {
		this.pk = pk;
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	public Venue getPromoter() {
		return promoter;
	}

	public void setPromoter(Venue promoter) {
		this.promoter = promoter;
	}

	public VenueRequestStatus getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(VenueRequestStatus requestStatus) {
		this.requestStatus = requestStatus;
	}

	public LocalDate getSince() {
		return since;
	}

	public void setSince(LocalDate since) {
		this.since = since;
	}
}
