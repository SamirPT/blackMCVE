package tech.peller.mrblackandroidwatch.models.seating;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import tech.peller.mrblackandroidwatch.models.user.facebook.FacebookInfo;
import tech.peller.mrblackandroidwatch.enums.VenueRequestStatusEnum;
import tech.peller.mrblackandroidwatch.enums.VenueRole;

/**
 * Created by arkady on 13/03/16
 */

public class VenueStaffInfo extends RealmObject implements Serializable {
    private Long id;
    private String fullName;
    private String email;
    private String userpic;
    private String phoneNumber;
    private String birthday;
    private RealmList<String> preferredRoles;
    private boolean hasFacebookProfile;
    private FacebookInfo facebookInfo;
    private boolean isAdmin;
    private String since;
    private String lastDate;
    private String requestStatus;

    public VenueStaffInfo() {
    }

    public VenueStaffInfo(Long id) {
        this.id = id;
    }

    public List<VenueRole> getPreferredRoles() {
        ArrayList<VenueRole> preferredRolesString = new ArrayList<>();
        for (String role : this.preferredRoles) preferredRolesString.add(VenueRole.valueOf(role));
        return preferredRolesString;
    }

    public void setPreferredRoles(List<VenueRole> preferredRoles) {
        RealmList<String> preferredRolesString = new RealmList<>();
        for (VenueRole role : preferredRoles) preferredRolesString.add(role.name());
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
        return DateTime.parse(birthday);
    }

    public void setBirthdayDateTime(DateTime birthday) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        this.birthday = fmt.print(birthday);
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

    public DateTime getSince() {
        //Date date=new Date(Long.valueOf(since));
        return new DateTime(since);
    }

    public void setSince(DateTime since) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        this.since = fmt.print(since);
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public VenueRequestStatusEnum getRequestStatus() {
        if(this.requestStatus != null) {
            return VenueRequestStatusEnum.valueOf(this.requestStatus);
        } else return null;
    }

    public void setRequestStatus(VenueRequestStatusEnum requestStatus) {
        this.requestStatus = requestStatus.name();
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return fullName;
    }
}
