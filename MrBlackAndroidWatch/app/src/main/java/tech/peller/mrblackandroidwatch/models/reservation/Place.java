package tech.peller.mrblackandroidwatch.models.reservation;

import io.realm.RealmObject;
import tech.peller.mrblackandroidwatch.enums.BottleServiceTypeEnum;
import tech.peller.mrblackandroidwatch.models.venue.Venue;

/**
 * Created by arkady on 07/03/16.
 */

public class Place extends RealmObject {


    private Integer id;


    private Venue venue;


    private String bottleServiceType;


    private String placeNumber;

    public Place() {
    }

    public Place(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public void setBottleService(BottleServiceTypeEnum bottleServiceType) {
        this.bottleServiceType = bottleServiceType.name();
    }

    public BottleServiceTypeEnum getBottleService() {
        return BottleServiceTypeEnum.valueOf(this.bottleServiceType);
    }

    public String getPlaceNumber() {
        return placeNumber;
    }

    public void setPlaceNumber(String placeNumber) {
        this.placeNumber = placeNumber;
    }
}
