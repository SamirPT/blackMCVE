package tech.peller.mrblackandroidwatch.models.reservation;


import java.util.List;

import tech.peller.mrblackandroidwatch.models.event.EventInfo;

public class ScheduleInfo {

    private List<EventInfo> eventInfos;
    private List<ReservationInfo> reservations;
    private List<TableWithSeating> tableAssignments;
    private String date;

    public List<EventInfo> getEventInfos() {
        return eventInfos;
    }

    public void setEventInfos(List<EventInfo> eventInfos) {
        this.eventInfos = eventInfos;
    }

    public List<ReservationInfo> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationInfo> reservations) {
        this.reservations = reservations;
    }

    public List<TableWithSeating> getTableAssignments() {
        return tableAssignments;
    }

    public void setTableAssignments(List<TableWithSeating> tableAssignments) {
        this.tableAssignments = tableAssignments;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
