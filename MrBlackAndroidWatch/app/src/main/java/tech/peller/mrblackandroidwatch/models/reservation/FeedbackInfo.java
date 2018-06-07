package tech.peller.mrblackandroidwatch.models.reservation;


import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import tech.peller.mrblackandroidwatch.enums.VenueRole;
import tech.peller.mrblackandroidwatch.models.user.UserInfo;

public class FeedbackInfo extends RealmObject {
    private Long id;
    private UserInfo author;
    private FeedbackReservationInfo shortReservationInfo;
    private String time;
    private String staffAssignment;
    private String message;
    private Integer rating;
    private String authorRole;
    private RealmList<String> tags;


    public List<String> getTags() {
        ArrayList<String> tagsArray = new ArrayList<>();
        tagsArray.addAll(this.tags);
        return tagsArray;
    }

    public void setTags(List<String> tagsList) {
        this.tags = new RealmList<>();
        this.tags.addAll(tagsList);
    }

    public VenueRole getAuthorRole() {
        return VenueRole.valueOf(authorRole) ;
    }

    public void setAuthorRole(VenueRole authorRole) {
        this.authorRole = authorRole.name();
    }

    public Integer getRating() {
        return (rating == null) ? 0 : rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStaffAssignment() {
        return staffAssignment;
    }

    public void setStaffAssignment(String staffAssignment) {
        this.staffAssignment = staffAssignment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public FeedbackReservationInfo getShortReservationInfo() {
        return shortReservationInfo;
    }

    public void setShortReservationInfo(FeedbackReservationInfo shortReservationInfo) {
        this.shortReservationInfo = shortReservationInfo;
    }

    public UserInfo getAuthor() {
        return author;
    }

    public void setAuthor(UserInfo author) {
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
