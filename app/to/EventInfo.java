package to;

import io.swagger.annotations.ApiModelProperty;
import models.Event;

/**
 * Created by arkady on 29/03/16.
 */
public class EventInfo {
	private Long id;
	@ApiModelProperty(required = true)
	private String name;
	private String description;
	private String fbEventUrl;
	@ApiModelProperty(required = true)
	private String startsAt;
	@ApiModelProperty(required = true)
	private String endsAt;
	@ApiModelProperty(required = true)
	private String date;
	@ApiModelProperty(required = true)
	private Long venueId;
	private Boolean repeatable;
	private String pictureUrl;

	public EventInfo() {
	}

	public EventInfo(Event event) {
		this();
		if (event == null) return;
		this.id = event.getId();
		this.name = event.getName();
		this.description = event.getDescription();
		this.fbEventUrl = event.getFbEventUrl();
		this.startsAt = event.getStartsAt().toString();
		this.endsAt = event.getEndsAt().toString();
		this.date = event.getDate().toString();
		this.venueId = event.getVenueId();
		this.repeatable = event.getRepeatable();
		this.pictureUrl = event.getPictureUrl();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFbEventUrl() {
		return fbEventUrl;
	}

	public void setFbEventUrl(String fbEventUrl) {
		this.fbEventUrl = fbEventUrl;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStartsAt() {
		return startsAt;
	}

	public void setStartsAt(String startsAt) {
		this.startsAt = startsAt;
	}

	public String getEndsAt() {
		return endsAt;
	}

	public void setEndsAt(String endsAt) {
		this.endsAt = endsAt;
	}

	public Long getVenueId() {
		return venueId;
	}

	public void setVenueId(Long venueId) {
		this.venueId = venueId;
	}

	public Boolean getRepeatable() {
		return repeatable;
	}

	public void setRepeatable(Boolean repeatable) {
		this.repeatable = repeatable;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	@Override
	public String toString() {
		return "EventInfo{" +
				"id=" + id +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", fbEventUrl='" + fbEventUrl + '\'' +
				", startsAt='" + startsAt + '\'' +
				", startsAt='" + endsAt + '\'' +
				", date='" + date + '\'' +
				", venueId=" + venueId +
				", repeatable=" + repeatable +
				", pictureUrl=" + pictureUrl +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EventInfo eventInfo = (EventInfo) o;

		if (id != null ? !id.equals(eventInfo.id) : eventInfo.id != null) return false;
		if (name != null ? !name.equals(eventInfo.name) : eventInfo.name != null) return false;
		if (description != null ? !description.equals(eventInfo.description) : eventInfo.description != null)
			return false;
		if (fbEventUrl != null ? !fbEventUrl.equals(eventInfo.fbEventUrl) : eventInfo.fbEventUrl != null) return false;
		if (startsAt != null ? !startsAt.equals(eventInfo.startsAt) : eventInfo.startsAt != null) return false;
		if (endsAt != null ? !endsAt.equals(eventInfo.endsAt) : eventInfo.endsAt != null) return false;
		if (date != null ? !date.equals(eventInfo.date) : eventInfo.date != null) return false;
		if (venueId != null ? !venueId.equals(eventInfo.venueId) : eventInfo.venueId != null) return false;
		if (repeatable != null ? !repeatable.equals(eventInfo.repeatable) : eventInfo.repeatable != null) return false;
		return pictureUrl != null ? pictureUrl.equals(eventInfo.pictureUrl) : eventInfo.pictureUrl == null;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (description != null ? description.hashCode() : 0);
		result = 31 * result + (fbEventUrl != null ? fbEventUrl.hashCode() : 0);
		result = 31 * result + (startsAt != null ? startsAt.hashCode() : 0);
		result = 31 * result + (endsAt != null ? endsAt.hashCode() : 0);
		result = 31 * result + (date != null ? date.hashCode() : 0);
		result = 31 * result + (venueId != null ? venueId.hashCode() : 0);
		result = 31 * result + (repeatable != null ? repeatable.hashCode() : 0);
		result = 31 * result + (pictureUrl != null ? pictureUrl.hashCode() : 0);
		return result;
	}
}
