package tech.peller.mrblackandroidwatch.models.event;

import org.joda.time.DateTime;

import java.io.Serializable;

import io.realm.RealmObject;
import tech.peller.mrblackandroidwatch.enums.DateFormatEnum;

/**
 * Created by arkady on 09/03/16.
 */


public class EventInfo extends RealmObject implements Serializable {

    private Long id;
    private String name;
    private String description;
    private String fbEventUrl;
    private String startsAt;
    private String endsAt;

    private String date;

    private Integer venueId;
    private Boolean repeatable;
    private Boolean deleted;
    private String pictureUrl;
    private String currentDate;

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


    public Integer getVenueId() {
        return venueId;
    }

    public void setVenueId(Integer venueId) {
        this.venueId = venueId;
    }

    public Boolean getRepeatable() {
        return repeatable;
    }

    public void setRepeatable(Boolean repeatable) {
        this.repeatable = repeatable;
    }

    public String getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(String startsAt) {
        this.startsAt = startsAt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(String endsAt) {
        this.endsAt = endsAt;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentDate(DateFormatEnum dateFormat) {
        String currentEventDate = currentDate;
        if (currentEventDate == null || currentEventDate.isEmpty())
            currentEventDate = DateTime.now().toString(DateFormatEnum.HEADER.toString());
        else
            currentEventDate = DateTime.parse(currentEventDate).toString(dateFormat.toString());
        return currentEventDate;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof EventInfo && ((EventInfo) o).getId().longValue() == getId().longValue());
    }
}