package tech.peller.mrblackandroidwatch.loader.reservations;

import tech.peller.mrblackandroidwatch.models.seating.StaffAssignment;

/**
 * Created by Sam (salyasov@gmail.com) on 07.01.2018
 */

public class LiveSpendTO {
    private Integer id;
    private Integer spend;
    private String description;
    private StaffAssignment user;
    private Integer reservationId;
    private String dateTime;
    private String role;

    public LiveSpendTO() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSpend() {
        return spend;
    }

    public void setSpend(Integer spend) {
        this.spend = spend;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StaffAssignment getUser() {
        return user;
    }

    public void setUser(StaffAssignment user) {
        this.user = user;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
