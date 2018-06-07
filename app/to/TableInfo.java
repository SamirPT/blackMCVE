package to;

import models.BottleServiceType;
import models.Place;

/**
 * Created by arkady on 14/03/16.
 */
public class TableInfo {
	private Long id;
	private Integer placeNumber;
	private BottleServiceType bottleServiceType;
	private boolean closed;

	public TableInfo() {
	}

	public TableInfo(Place place) {
		this();
		if (place == null) return;
		this.id = place.getId();
		this.placeNumber = place.getPlaceNumber();
		this.bottleServiceType = place.getBottleServiceType();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getPlaceNumber() {
		return placeNumber;
	}

	public void setPlaceNumber(Integer placeNumber) {
		this.placeNumber = placeNumber;
	}

	public BottleServiceType getBottleServiceType() {
		return bottleServiceType;
	}

	public void setBottleServiceType(BottleServiceType bottleServiceType) {
		this.bottleServiceType = bottleServiceType;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	@Override
	public String toString() {
		return "TableInfo{" +
				"id=" + id +
				", placeNumber=" + placeNumber +
				", bottleServiceType=" + bottleServiceType +
				'}';
	}
}
