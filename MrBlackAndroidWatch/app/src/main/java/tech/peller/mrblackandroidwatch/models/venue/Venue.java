package tech.peller.mrblackandroidwatch.models.venue;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import tech.peller.mrblackandroidwatch.enums.VenueRole;
import tech.peller.mrblackandroidwatch.enums.VenueTypeEnum;
import tech.peller.mrblackandroidwatch.retrofit.RetrofitExclude;

/**
 * Created by arkady on 15/02/16
 */


public class Venue extends RealmObject implements Serializable, Parcelable, Cloneable {

    public static final Creator<Venue> CREATOR = new Creator<Venue>() {
        @Override
        public Venue createFromParcel(Parcel in) {
            return new Venue(in);
        }

        @Override
        public Venue[] newArray(int size) {
            return new Venue[size];
        }
    };
    private Long id;
    private String fbPlaceId;
    private String coverUrl;
    private String logoUrl;
    private String venueType;
    private String name;
    private String address;
    private Integer capacity;
    private String timeZone = DateTime.now().getZone().getID(); //TODO get timezone from venue address
    private String phoneNumber;
    private String city;
    private String state;
    private String country;
    private String zip;
    private String unit;
    private String email;
    private String website;
    private String facebookPageUrl;
    private String shortName;
    private String embedFormTextColor;
    private String embedFormBGColor;
    private String embedFormReservationsType;
    private Boolean individual;
    private String restaurantAddress;
    private String restaurantLogoUrl;
    private String restaurantUrl;
    private String restaurantName;
    private RealmList<String> tags;
    private RealmList<ImageWithTitle> barImages;
    private RealmList<ImageWithTitle> floorPlanImages;
    private Boolean sendBSConfirmation;
    private Boolean sendGLConfirmation;
    private Boolean hasBS;
    private String activeUntilDate;
    private Boolean active;
    private String locationCid;
    private String floorPlanUrl;
    private String barUrl;
    @Deprecated
    @RetrofitExclude(Deserialization = false)
    private RealmList<PromotionCompany> promoCompanies;
    @Deprecated
    @RetrofitExclude(Deserialization = false)
    private String requestStatus;
    @Deprecated
    @RetrofitExclude(Deserialization = false)
    private RealmList<String> preferredRoles;
    @Deprecated
    @RetrofitExclude(Deserialization = false)
    private Integer unreadNotifications;
    @Deprecated
    @RetrofitExclude(Deserialization = false)
    private Boolean favorite;
    private Boolean showMinSpend;
    private Boolean showMinBottles;
    private String stripeFailedInvoiceId;
    private Float lat;
    private Float lng;
    private Boolean showInGuestApp;
    private String favoriteFromDateTime;
    private String favoriteFromDate;
    private String favoriteFromTime;
    private Boolean showPlans;
    private String billingEmail;

    public Venue() {
    }

    public Venue(PromoterRequest promoterRequest) {
        this.venueType = promoterRequest.getVenueType().name();
        this.id = promoterRequest.getId();
        this.name = promoterRequest.getName();
        this.address = promoterRequest.getAddress();
        this.fbPlaceId = promoterRequest.getFbPlaceId();
        this.coverUrl = promoterRequest.getCoverUrl();
        this.logoUrl = promoterRequest.getLogoUrl();
        this.capacity = promoterRequest.getCapacity();
        this.timeZone = promoterRequest.getTimeZone();
        this.requestStatus = promoterRequest.getRequestStatus().name();
    }

    protected Venue(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        fbPlaceId = in.readString();
        coverUrl = in.readString();
        logoUrl = in.readString();
        venueType = in.readString();
        name = in.readString();
        address = in.readString();
        if (in.readByte() == 0) {
            capacity = null;
        } else {
            capacity = in.readInt();
        }
        timeZone = in.readString();
        phoneNumber = in.readString();
        city = in.readString();
        state = in.readString();
        country = in.readString();
        zip = in.readString();
        unit = in.readString();
        email = in.readString();
        website = in.readString();
        facebookPageUrl = in.readString();
        shortName = in.readString();
        embedFormTextColor = in.readString();
        embedFormBGColor = in.readString();
        embedFormReservationsType = in.readString();
        byte tmpIndividual = in.readByte();
        individual = tmpIndividual == 0 ? null : tmpIndividual == 1;
        restaurantAddress = in.readString();
        restaurantLogoUrl = in.readString();
        restaurantUrl = in.readString();
        restaurantName = in.readString();
        byte tmpSendBSConfirmation = in.readByte();
        sendBSConfirmation = tmpSendBSConfirmation == 0 ? null : tmpSendBSConfirmation == 1;
        byte tmpSendGLConfirmation = in.readByte();
        sendGLConfirmation = tmpSendGLConfirmation == 0 ? null : tmpSendGLConfirmation == 1;
        hasBS = in.readByte() != 0;
        activeUntilDate = in.readString();
        byte tmpActive = in.readByte();
        active = tmpActive == 0 ? null : tmpActive == 1;
        locationCid = in.readString();
        floorPlanUrl = in.readString();
        barUrl = in.readString();
        requestStatus = in.readString();
        if (in.readByte() == 0) {
            unreadNotifications = null;
        } else {
            unreadNotifications = in.readInt();
        }
        byte tmpFavorite = in.readByte();
        favorite = tmpFavorite == 0 ? null : tmpFavorite == 1;
        byte tmpShowMinSpend = in.readByte();
        showMinSpend = tmpShowMinSpend == 0 ? null : tmpShowMinSpend == 1;
        byte tmpShowMinBottles = in.readByte();
        showMinBottles = tmpShowMinBottles == 0 ? null : tmpShowMinBottles == 1;
        showPlans = in.readByte() != 0;
        billingEmail = in.readByte() == 0 ? null : in.readString();
    }

    public Boolean getIndividual() {
        return individual;
    }

    public void setIndividual(Boolean individual) {
        this.individual = individual;
    }

    public Boolean getSendBSConfirmation() {
        return sendBSConfirmation;
    }

    public void setSendBSConfirmation(Boolean sendBSConfirmation) {
        this.sendBSConfirmation = sendBSConfirmation;
    }

    public Boolean getSendGLConfirmation() {
        return sendGLConfirmation;
    }

    public void setSendGLConfirmation(Boolean sendGLConfirmation) {
        this.sendGLConfirmation = sendGLConfirmation;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getStripeFailedInvoiceId() {
        return stripeFailedInvoiceId;
    }

    public void setStripeFailedInvoiceId(String stripeFailedInvoiceId) {
        this.stripeFailedInvoiceId = stripeFailedInvoiceId;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public Boolean getShowInGuestApp() {
        return showInGuestApp;
    }

    public void setShowInGuestApp(Boolean showInGuestApp) {
        this.showInGuestApp = showInGuestApp;
    }

    public Boolean getShowPlans() {
        return showPlans;
    }

    public void setShowPlans(Boolean showPlans) {
        this.showPlans = showPlans;
    }

    public String getBillingEmail() {return billingEmail;}

    public void setBillingEmail(String billingEmail) {this.billingEmail = billingEmail;}

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(fbPlaceId);
        dest.writeString(coverUrl);
        dest.writeString(logoUrl);
        dest.writeString(venueType);
        dest.writeString(name);
        dest.writeString(address);
        if (capacity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(capacity);
        }
        dest.writeString(timeZone);
        dest.writeString(phoneNumber);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(country);
        dest.writeString(zip);
        dest.writeString(unit);
        dest.writeString(email);
        dest.writeString(website);
        dest.writeString(facebookPageUrl);
        dest.writeString(shortName);
        dest.writeString(embedFormTextColor);
        dest.writeString(embedFormBGColor);
        dest.writeString(embedFormReservationsType);
        dest.writeByte((byte) (individual == null ? 0 : individual ? 1 : 2));
        dest.writeString(restaurantAddress);
        dest.writeString(restaurantLogoUrl);
        dest.writeString(restaurantUrl);
        dest.writeString(restaurantName);
        dest.writeByte((byte) (sendBSConfirmation == null ? 0 : sendBSConfirmation ? 1 : 2));
        dest.writeByte((byte) (sendGLConfirmation == null ? 0 : sendGLConfirmation ? 1 : 2));
        dest.writeByte((byte) (hasBS ? 1 : 0));
        dest.writeString(activeUntilDate);
        dest.writeByte((byte) (active == null ? 0 : active ? 1 : 2));
        dest.writeString(locationCid);
        dest.writeString(floorPlanUrl);
        dest.writeString(barUrl);
        dest.writeString(requestStatus);
        if (unreadNotifications == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(unreadNotifications);
        }
        dest.writeByte((byte) (favorite == null ? 0 : favorite ? 1 : 2));
        dest.writeByte((byte) (showMinSpend == null ? 0 : showMinSpend ? 1 : 2));
        dest.writeByte((byte) (showMinBottles == null ? 0 : showMinBottles ? 1 : 2));
        dest.writeByte((byte) (showPlans ? 1 : 0));
        dest.writeString(billingEmail);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setVenueType(String venueType) {
        this.venueType = venueType;
    }

    public String getEmbedFormTextColor() {
        return embedFormTextColor;
    }

    public void setEmbedFormTextColor(String embedFormTextColor) {
        this.embedFormTextColor = embedFormTextColor;
    }

    public String getEmbedFormBGColor() {
        return embedFormBGColor;
    }

    public void setEmbedFormBGColor(String embedFormBGColor) {
        this.embedFormBGColor = embedFormBGColor;
    }

    public String getEmbedFormReservationsType() {
        return embedFormReservationsType;
    }

    public void setEmbedFormReservationsType(String embedFormReservationsType) {
        this.embedFormReservationsType = embedFormReservationsType;
    }

    public boolean isIndividual() {
        return individual;
    }

    public void setIndividual(boolean individual) {
        this.individual = individual;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getRestaurantLogoUrl() {
        return restaurantLogoUrl;
    }

    public void setRestaurantLogoUrl(String restaurantLogoUrl) {
        this.restaurantLogoUrl = restaurantLogoUrl;
    }

    public String getRestaurantUrl() {
        return restaurantUrl;
    }

    public void setRestaurantUrl(String restaurantUrl) {
        this.restaurantUrl = restaurantUrl;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public void setTags(RealmList<String> tags) {
        this.tags = tags;
    }

    public void setBarImages(RealmList<ImageWithTitle> barImages) {
        this.barImages = barImages;
    }

    public void setFloorPlanImages(RealmList<ImageWithTitle> floorPlanImages) {
        this.floorPlanImages = floorPlanImages;
    }

    public boolean isSendBSConfirmation() {
        return sendBSConfirmation;
    }

    public void setSendBSConfirmation(boolean sendBSConfirmation) {
        this.sendBSConfirmation = sendBSConfirmation;
    }

    public boolean isSendGLConfirmation() {
        return sendGLConfirmation;
    }

    public void setSendGLConfirmation(boolean sendGLConfirmation) {
        this.sendGLConfirmation = sendGLConfirmation;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getLocationCid() {
        return locationCid;
    }

    public void setLocationCid(String locationCid) {
        this.locationCid = locationCid;
    }

    public String getFloorPlanUrl() {
        return floorPlanUrl;
    }

    public void setFloorPlanUrl(String floorPlanUrl) {
        this.floorPlanUrl = floorPlanUrl;
    }

    public String getBarUrl() {
        return barUrl;
    }

    public void setBarUrl(String barUrl) {
        this.barUrl = barUrl;
    }

    public void setPromoCompanies(RealmList<PromotionCompany> promoCompanies) {
        this.promoCompanies = promoCompanies;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFacebookPageUrl() {
        return facebookPageUrl;
    }

    public void setFacebookPageUrl(String facebookPageUrl) {
        this.facebookPageUrl = facebookPageUrl;
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

    public List<PromotionCompany> getPromoCompanies() {
        return this.promoCompanies;
    }

    public void setPromoCompanies(List<PromotionCompany> list) {
        RealmList<PromotionCompany> promoCompanies = new RealmList<>();
        promoCompanies.addAll(list);
        this.promoCompanies = promoCompanies;
    }

    public VenueRequestStatus getRequestStatus() {
        if (this.requestStatus != null) {
            return VenueRequestStatus.valueOf(this.requestStatus);
        } else return null;
    }

    public void setRequestStatus(VenueRequestStatus requestStatus) {
        this.requestStatus = (requestStatus == null) ? null : requestStatus.name();
    }

    public List<VenueRole> getPreferredRoles() {
        ArrayList<VenueRole> preferredRolesString = new ArrayList<>();
        if (this.preferredRoles != null) {
            for (String role : this.preferredRoles)
                //предупреждаем ошибки при появлении новых ролей, пока они не добавлены в enum
                try {
                    preferredRolesString.add(VenueRole.valueOf(role));
                } catch (IllegalArgumentException e) {
                    Log.d("Venue", "getPreferredRoles: IllegalArgumentException - " +
                            role);
                }
        }
        return preferredRolesString;
    }

    public void setPreferredRoles(List<VenueRole> preferredRoles) {
        RealmList<String> preferredRolesString = new RealmList<>();
        for (VenueRole role : preferredRoles)
            preferredRolesString.add(role.name());
        this.preferredRoles = preferredRolesString;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getFbPlaceId() {
        return fbPlaceId;
    }

    public void setFbPlaceId(String fbPlaceId) {
        this.fbPlaceId = fbPlaceId;
    }

    public List<String> getTags() {
        ArrayList<String> tagsList = new ArrayList<>();
        if (this.tags != null) {
            tagsList.addAll(this.tags);
        }
        return tagsList;
    }

    public void setTags(List<String> tags) {
        RealmList<String> tagList = new RealmList<>();
        for (String tag : tags) tagList.add(new String(tag));
        this.tags = tagList;
    }

    public VenueTypeEnum getVenueType() {
        if (this.venueType != null) {
            return VenueTypeEnum.valueOf(this.venueType);
        } else {
            return null;
        }
    }

    public void setVenueType(VenueTypeEnum venueType) {
        this.venueType = (venueType == null) ? null : venueType.name();
    }

    public Integer getUnreadNotifications() {
        return unreadNotifications;
    }

    public void setUnreadNotifications(Integer unreadNotifications) {
        this.unreadNotifications = unreadNotifications;
    }

    public List<ImageWithTitle> getBarImages() {
        return barImages;
    }

    public void setBarImages(ArrayList<ImageWithTitle> barImages) {
        RealmList<ImageWithTitle> barImagesList = new RealmList<>();
        for (ImageWithTitle element : barImages) barImagesList.add(element);
        this.barImages = barImagesList;
    }

    public List<ImageWithTitle> getFloorPlanImages() {
        return floorPlanImages;
    }

    public void setFloorPlanImages(ArrayList<ImageWithTitle> floorPlanImages) {
        RealmList<ImageWithTitle> floorPlanImagesList = new RealmList<>();
        for (ImageWithTitle element : floorPlanImages) floorPlanImagesList.add(element);
        this.floorPlanImages = floorPlanImagesList;
    }

    public Boolean getHasBS() {
        return hasBS;
    }

    public void setHasBS(Boolean hasBS) {
        this.hasBS = hasBS;
    }

    public String getActiveUntilDate() {
        return activeUntilDate;
    }

    public void setActiveUntilDate(String activeUntilDate) {
        this.activeUntilDate = activeUntilDate;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public Boolean getShowMinSpend() {
        return showMinSpend;
    }

    public void setShowMinSpend(Boolean showMinSpend) {
        this.showMinSpend = showMinSpend;
    }

    public Boolean getShowMinBottles() {
        return showMinBottles;
    }

    public void setShowMinBottles(Boolean showMinBottles) {
        this.showMinBottles = showMinBottles;
    }

}
