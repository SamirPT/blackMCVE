package tech.peller.mrblackandroidwatch.models.seating;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Sam (samir@peller.tech) on 27.05.2016
 */
public class StaffAssignment extends RealmObject implements Serializable, Parcelable {

    public static final Creator<StaffAssignment> CREATOR = new Creator<StaffAssignment>() {
        @Override
        public StaffAssignment createFromParcel(Parcel in) {
            return new StaffAssignment(in);
        }

        @Override
        public StaffAssignment[] newArray(int size) {
            return new StaffAssignment[size];
        }
    };
    private Long id;
    private String name;
    private String phone;
    private String role;
    private String birthday;
    private String email;
    private String userpic;

    public StaffAssignment() {
    }


    public StaffAssignment(Long id, String name, String phone, String role, String birthday, String email, String userpic) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.birthday = birthday;
        this.email = email;
        this.userpic = userpic;
    }

    protected StaffAssignment(Parcel in) {
        id = (Long) in.readSerializable();
        name = in.readString();
        phone = in.readString();
        role = in.readString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof StaffAssignment)) return false;
        return ((StaffAssignment) o).getId().equals(this.id);
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserpic() {
        return userpic;
    }

    public void setUserpic(String userpic) {
        this.userpic = userpic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(id);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(role);
        dest.writeString(birthday);
        dest.writeString(email);
        dest.writeString(userpic);
    }
}
