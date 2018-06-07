package models;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Created by arkady on 08/08/16.
 */
@Embeddable
public class PromoterVenuePK {
	@Column(name = "promoter_id")
	private Long promoterId;
	@Column(name = "venue_id")
	private Long venueId;

	public PromoterVenuePK() {
	}

	public PromoterVenuePK(Long promoterId, Long venueId) {
		this.promoterId = promoterId;
		this.venueId = venueId;
	}

	public Long getPromoterId() {
		return promoterId;
	}

	public void setPromoterId(Long promoterId) {
		this.promoterId = promoterId;
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

		PromoterVenuePK that = (PromoterVenuePK) o;

		if (promoterId != null ? !promoterId.equals(that.promoterId) : that.promoterId != null) return false;
		return venueId != null ? venueId.equals(that.venueId) : that.venueId == null;

	}

	@Override
	public int hashCode() {
		int result = promoterId != null ? promoterId.hashCode() : 0;
		result = 31 * result + (venueId != null ? venueId.hashCode() : 0);
		return result;
	}
}
