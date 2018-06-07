package tech.peller.mrblackandroidwatch.models.reservation;


import java.io.Serializable;

import io.realm.RealmObject;
import tech.peller.mrblackandroidwatch.enums.BottleServiceTypeEnum;

/**
 * Created by arkady on 14/03/16.
 */
public class TableInfo extends RealmObject implements Serializable {
    private Integer id;
    private String placeNumber;
    private String bottleServiceType;
    private boolean closed;
    private Integer orderIndex;
    private Long sectionId;
    private Integer minSpend;

    public TableInfo() {
    }

    public TableInfo(Place place) {
        this();
        if (place == null) return;
        this.id = place.getId();
        this.placeNumber = place.getPlaceNumber();
        this.bottleServiceType = place.getBottleService().name();
    }

    public String getTableTitle() {
        try {
            return getBottleService().name().substring(0, 1) + getPlaceNumber();
        } catch (NullPointerException e) {
            return "";
        }
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlaceNumber() {
        return placeNumber;
    }

    public void setPlaceNumber(String placeNumber) {
        this.placeNumber = placeNumber;
    }

    public BottleServiceTypeEnum getBottleService() {
        if(this.bottleServiceType != null) {
            return BottleServiceTypeEnum.valueOf(this.bottleServiceType);
        }
        return null;
    }

    public void setBottleService(BottleServiceTypeEnum bottleServiceType) {
        this.bottleServiceType = bottleServiceType.name();
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Integer getMinSpend() {
        return minSpend;
    }

    public void setMinSpend(Integer minSpend) {
        this.minSpend = minSpend;
    }
}
