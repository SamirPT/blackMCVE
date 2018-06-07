package models;

import com.avaje.ebean.Model;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

/**
 * Created by arkady on 07/03/16.
 */
@Entity
public class Place extends Model {

	@Id
	@ApiModelProperty(readOnly = true)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "venue_id", referencedColumnName = "id", nullable = false)
	private Venue venue;

	@ApiModelProperty(required = true)
	@Enumerated(EnumType.STRING)
	private BottleServiceType bottleServiceType;

	@ApiModelProperty(required = true)
	private Integer placeNumber;

	public static Finder<Long, Place> find = new Finder<>(Place.class);

	public Place() {
	}

	public Place(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	public BottleServiceType getBottleServiceType() {
		return bottleServiceType;
	}

	public void setBottleServiceType(BottleServiceType bottleServiceType) {
		this.bottleServiceType = bottleServiceType;
	}

	public Integer getPlaceNumber() {
		return placeNumber;
	}

	public void setPlaceNumber(Integer placeNumber) {
		this.placeNumber = placeNumber;
	}
}
