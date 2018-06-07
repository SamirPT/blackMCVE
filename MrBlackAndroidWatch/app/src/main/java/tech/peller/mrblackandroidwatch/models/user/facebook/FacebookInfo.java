package tech.peller.mrblackandroidwatch.models.user.facebook;

import io.realm.RealmObject;

/**
 * Created by arkady on 03/02/16.
 */


public class FacebookInfo extends RealmObject {


    private String id;

    private String name;

    private String pictureUrl;

    private String email;

    private String birthday;

    private String token;

    private Long friendsNumber;

    private FacebookLocation location;

    private String gender;

    private String relationshipStatus;

    private FbPicture picture;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getRelationshipStatus() {
        return relationshipStatus;
    }

    public void setRelationshipStatus(String relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public FacebookLocation getLocation() {
        return location;
    }

    public void setLocation(FacebookLocation location) {
        this.location = location;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public FbPicture getPicture() {
        return picture;
    }

    public void setPicture(FbPicture picture) {
        this.picture = picture;
    }

    public Long getFriendsNumber() {
        return friendsNumber;
    }

    public void setFriendsNumber(Long friendsNumber) {
        this.friendsNumber = friendsNumber;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FacebookInfo that = (FacebookInfo) o;

        if (!id.equals(that.id)) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (pictureUrl != null ? !pictureUrl.equals(that.pictureUrl) : that.pictureUrl != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (birthday != null ? !birthday.equals(that.birthday) : that.birthday != null) return false;
        if (relationshipStatus != null ? !relationshipStatus.equals(that.relationshipStatus) : that.relationshipStatus != null)
            return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        return !(gender != null ? !gender.equals(that.gender) : that.gender != null);

    }


    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (pictureUrl != null ? pictureUrl.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (relationshipStatus != null ? relationshipStatus.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        return result;
    }
}