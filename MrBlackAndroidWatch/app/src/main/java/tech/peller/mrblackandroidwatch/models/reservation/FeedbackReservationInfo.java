package tech.peller.mrblackandroidwatch.models.reservation;


import io.realm.RealmObject;

public class FeedbackReservationInfo extends RealmObject {
    private Long reservationId;
    private String reservationDate;

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }



}
