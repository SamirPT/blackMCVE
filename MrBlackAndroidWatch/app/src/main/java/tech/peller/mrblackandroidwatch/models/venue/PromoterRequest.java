package tech.peller.mrblackandroidwatch.models.venue;

import java.io.Serializable;
import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import tech.peller.mrblackandroidwatch.enums.VenueTypeEnum;

/**
 * Created by Sam (samir@peller.tech) on 09.12.2016
 */

public class PromoterRequest extends RealmObject implements Serializable {
    private String venueType;
    private Long id;
    private String name;
    private String address;
    private String fbPlaceId;
    private String coverUrl;
    private String logoUrl;
    private Integer capacity;
    private String timeZone;
    private RealmList<String> tags;
    private String since;
    private String requestStatus;

    public PromoterRequest() {}

    public VenueTypeEnum getVenueType() {
        return VenueTypeEnum.valueOf(venueType);
    }

    public void setVenueType(VenueTypeEnum venueType) {
        this.venueType = venueType.name();
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFbPlaceId() {
        return fbPlaceId;
    }

    public void setFbPlaceId(String fbPlaceId) {
        this.fbPlaceId = fbPlaceId;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public ArrayList<String> getTags() {
        ArrayList<String> tagsList = new ArrayList<>();
        if (this.tags != null) {
            tagsList.addAll(this.tags);
        }
        return tagsList;
    }

    public void setTags(ArrayList<String> tags) {
        RealmList<String> tagList = new RealmList<>();
        tagList.addAll(tags);
        this.tags = tagList;
    }

    public String getSince() {
        return since;
    }

    public void setSince(String since) {
        this.since = since;
    }

    public VenueRequestStatus getRequestStatus() {
        return VenueRequestStatus.valueOf(requestStatus);
    }

    public void setRequestStatus(VenueRequestStatus requestStatus) {
        this.requestStatus = requestStatus.name();
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isPrevious() {
        return VenueRequestStatus.PREVIOUS.name().equals(requestStatus);
    }
}
