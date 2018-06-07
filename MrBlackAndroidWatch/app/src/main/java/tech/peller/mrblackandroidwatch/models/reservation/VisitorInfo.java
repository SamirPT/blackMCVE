package tech.peller.mrblackandroidwatch.models.reservation;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import tech.peller.mrblackandroidwatch.enums.VenueRole;
import tech.peller.mrblackandroidwatch.models.user.facebook.FacebookInfo;


public class VisitorInfo extends RealmObject implements Serializable {
    private Long guestInfoId;

    private Long id;
    private String fullName;
    private String email;
    private String userpic;
    private String phoneNumber;
    private String birthday;
    private RealmList<String> preferredRoles;
    //    private JsonNode preferredRoles;
    private boolean hasFacebookProfile;
    private FacebookInfo facebookInfo;
    private Boolean isAdmin;
    private Short rating;
    private Integer totalVisits;
    private Integer bsVisits;
    private Integer glVisits;
    private Integer totalReservations;
    private Integer glReservations;
    private Integer feedbackCount;
    private Integer bsReservations;
    private Integer totalSpent;
    private Integer avgSpent;
    private String firstVisitDate;
    private String lastVisitDate;
    private String lastReservationDate;
    private String lastBSReservationDate;
    private String lastGLReservationDate;
    private String companyName;
    private String city;
    private String country;
    private String state;
    private String address;
    private String zip;
    private String unit;

    public VisitorInfo() {
    }

    public VisitorInfo(Long id) {
        this.id = id;
    }

    public VisitorInfo(SearchUserInfo searchUserInfo) {
        this.id = searchUserInfo.getId();
        this.guestInfoId = searchUserInfo.getGuestInfoId();
        this.fullName = searchUserInfo.getName();
        this.phoneNumber = searchUserInfo.getPhone();
        this.birthday = searchUserInfo.getBirthday();
        this.email = searchUserInfo.getEmail();
    }

    public void setPreferredRoles(RealmList<String> preferredRoles) {
        this.preferredRoles = preferredRoles;
    }

    public Long getGuestInfoId() {
        return guestInfoId;
    }

    public void setGuestInfoId(Long guestInfoId) {
        this.guestInfoId = guestInfoId;
    }

    public Integer getAvgSpent() {
        return avgSpent;
    }

    public void setAvgSpent(Integer avgSpent) {
        this.avgSpent = avgSpent;
    }

    public String getFirstVisitDate() {
        return firstVisitDate;
    }

    public void setFirstVisitDate(String firstVisitDate) {
        this.firstVisitDate = firstVisitDate;
    }

    public String getLastVisitDate() {
        return lastVisitDate;
    }

    public void setLastVisitDate(String lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    public String getLastBSReservationDate() {
        return lastBSReservationDate;
    }

    public void setLastBSReservationDate(String lastBSReservationDate) {
        this.lastBSReservationDate = lastBSReservationDate;
    }

    public String getLastGLReservationDate() {
        return lastGLReservationDate;
    }

    public void setLastGLReservationDate(String lastGLReservationDate) {
        this.lastGLReservationDate = lastGLReservationDate;
    }

    public List<VenueRole> getPreferredRoles() {
        ArrayList<VenueRole> preferredRolesString = new ArrayList<>();
        for (String role : this.preferredRoles)
            preferredRolesString.add(VenueRole.valueOf(role));
        return preferredRolesString;
    }

    public void setPreferredRoles(List<VenueRole> preferredRoles) {
        RealmList<String> preferredRolesString = new RealmList<>();
        for (VenueRole role : preferredRoles)
            preferredRolesString.add(role.name());
        this.preferredRoles = preferredRolesString;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public DateTime getBirthdayDateTime() {
        if (birthday != null && !birthday.isEmpty()) {
            return DateTime.parse(birthday);
        } else {
            return null;
        }
    }

    public void setBirthdayDateTime(DateTime birthday) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        String str = fmt.print(birthday);
        this.birthday = str;
    }

    public FacebookInfo getFacebookInfo() {
        return facebookInfo;
    }

    public void setFacebookInfo(FacebookInfo facebookInfo) {
        this.facebookInfo = facebookInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserpic() {
        return userpic;
    }

    public void setUserpic(String userpic) {
        this.userpic = userpic;
    }

    public boolean isHasFacebookProfile() {
        return hasFacebookProfile;
    }

    public void setHasFacebookProfile(boolean hasFacebookProfile) {
        this.hasFacebookProfile = hasFacebookProfile;
    }

    public short getRating() {
        if (rating == null) {
            return (short) 0;
        } else {
            return rating;
        }
    }

    public void setRating(short rating) {
        this.rating = rating;
    }

    public Integer getTotalVisits() {
        return totalVisits == null ? 0 : totalVisits;
    }

    public void setTotalVisits(int totalVisits) {
        this.totalVisits = totalVisits;
    }

    public Integer getTotalReservations() {
        return totalReservations == null ? 0 : totalReservations;
    }

    public void setTotalReservations(int totalReservations) {
        this.totalReservations = totalReservations;
    }

    public Integer getFeedbackCount() {
        return feedbackCount == null ? 0 : feedbackCount;
    }

    public void setFeedbackCount(int feedbackCount) {
        this.feedbackCount = feedbackCount;
    }

    public Boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Integer getBsVisits() {
        return bsVisits == null ? 0 : bsVisits;
    }

    public void setBsVisits(Integer bsVisits) {
        this.bsVisits = bsVisits;
    }

    public Integer getGlVisits() {
        return glVisits == null ? 0 : glVisits;
    }

    public void setGlVisits(Integer glVisits) {
        this.glVisits = glVisits;
    }

    public Integer getGlReservations() {
        return glReservations == null ? 0 : glReservations;
    }

    public void setGlReservations(Integer glReservations) {
        this.glReservations = glReservations;
    }

    public Integer getBsReservations() {
        return bsReservations == null ? 0 : bsReservations;
    }

    public void setBsReservations(Integer bsReservations) {
        this.bsReservations = bsReservations;
    }

    public Integer getTotalSpent() {
        return totalSpent == null ? 0 : totalSpent;
    }

    public void setTotalSpent(Integer totalSpent) {
        this.totalSpent = totalSpent;
    }

    public String getLastReservationDate() {
        return lastReservationDate;
    }

    public void setLastReservationDate(String lastReservationDate) {
        this.lastReservationDate = lastReservationDate;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
}
