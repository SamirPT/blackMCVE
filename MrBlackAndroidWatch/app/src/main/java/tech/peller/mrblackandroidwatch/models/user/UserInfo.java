package tech.peller.mrblackandroidwatch.models.user;


import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import tech.peller.mrblackandroidwatch.models.seating.VenueStaffInfo;
import tech.peller.mrblackandroidwatch.models.user.facebook.FacebookInfo;
import tech.peller.mrblackandroidwatch.models.seating.StaffAssignment;
import tech.peller.mrblackandroidwatch.enums.VenueRole;
import tech.peller.mrblackandroidwatch.enums.DateFormatEnum;
import tech.peller.mrblackandroidwatch.utils.StringFormatter;

/**
 * Created by arkady on 12/02/16.
 */

public class UserInfo extends RealmObject implements Parcelable, Cloneable {

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
    private int id;
    private String fullName;
    private String email;
    private String userpic;
    private String phoneNumber;
    private String birthday;
    private RealmList<String> preferredRoles;
    private boolean hasFacebookProfile;
    private FacebookInfo facebookInfo;
    private boolean isAdmin;

    public UserInfo() {
    }

    public UserInfo(int id, String fullName, String email, String userpic, String phoneNumber, String birthday, RealmList<String> preferredRoles, boolean hasFacebookProfile, FacebookInfo facebookInfo, Boolean isAdmin) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.userpic = userpic;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
        this.preferredRoles = preferredRoles;
        this.hasFacebookProfile = hasFacebookProfile;
        this.facebookInfo = facebookInfo;
        this.isAdmin = isAdmin;
    }

    public UserInfo(VenueStaffInfo venueStaffInfo) {
        if (venueStaffInfo == null) return;
        this.id = venueStaffInfo.getId().intValue();
        this.fullName = venueStaffInfo.getFullName();
        this.email = venueStaffInfo.getEmail();
        this.userpic = venueStaffInfo.getUserpic();
        this.phoneNumber = venueStaffInfo.getPhoneNumber();
        this.birthday = venueStaffInfo.getBirthday();
        setPreferredRoles(venueStaffInfo.getPreferredRoles());
        this.facebookInfo = venueStaffInfo.getFacebookInfo();
        this.isAdmin = venueStaffInfo.isAdmin();
    }

    public UserInfo(StaffAssignment staffAssignment) {
        this.id = staffAssignment.getId().intValue();
        this.fullName = staffAssignment.getName();
        this.email = staffAssignment.getEmail();
        this.userpic = staffAssignment.getUserpic();
        this.phoneNumber = staffAssignment.getPhone();
        this.birthday = staffAssignment.getBirthday();
    }

    protected UserInfo(Parcel in) {
        id = in.readInt();
        fullName = in.readString();
        email = in.readString();
        userpic = in.readString();
        phoneNumber = in.readString();
        birthday = in.readString();
        hasFacebookProfile = in.readByte() != 0;
        isAdmin = in.readByte() != 0;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        if (this.birthday != null) {
            DateTime birthday = DateTime.parse(this.birthday, DateTimeFormat.forPattern("dd/MM/yyyy"));
            return birthday;
        } else return null;
    }

    public void setBirthdayDateTime(DateTime birthday) {
//        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
//        String str = fmt.print(birthday);
//        this.birthday = str;
        this.birthday = StringFormatter.formatDate(birthday, DateFormatEnum.SERVER.toString());
    }

    public List<VenueRole> getPreferredRoles() {
        ArrayList<VenueRole> preferredRolesString = new ArrayList<>();
        if (this.preferredRoles != null) {
            if (!this.preferredRoles.isEmpty()) {
                for (String role : this.preferredRoles)
                    preferredRolesString.add(VenueRole.valueOf(role));
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

    public String getUserpic() {
        return userpic;
    }

    public void setUserpic(String userpic) {
        this.userpic = userpic;
    }

    public boolean isHasFacebookProfile() {
        return hasFacebookProfile;
    }

    public FacebookInfo getFacebookInfo() {
        return facebookInfo;
    }

    public void setFacebookInfo(FacebookInfo facebookInfo) {
        this.hasFacebookProfile = (facebookInfo != null);
        this.facebookInfo = facebookInfo;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(fullName);
        dest.writeString(email);
        dest.writeString(userpic);
        dest.writeString(phoneNumber);
        dest.writeString(birthday);
        dest.writeByte((byte) (hasFacebookProfile ? 1 : 0));
        dest.writeByte((byte) (isAdmin ? 1 : 0));
    }

    @Override
    public UserInfo clone() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(this.getId());
        userInfo.setFullName(this.getFullName());
        userInfo.setEmail(this.getEmail());
        userInfo.setUserpic(this.getUserpic());
        userInfo.setPhoneNumber(this.getPhoneNumber());
        userInfo.setBirthday(this.getBirthday());
        userInfo.setPreferredRoles(this.getPreferredRoles());
        userInfo.hasFacebookProfile = this.isHasFacebookProfile();
        userInfo.setFacebookInfo(this.getFacebookInfo());
        userInfo.setIsAdmin(this.isAdmin());
        return userInfo;
    }
}
