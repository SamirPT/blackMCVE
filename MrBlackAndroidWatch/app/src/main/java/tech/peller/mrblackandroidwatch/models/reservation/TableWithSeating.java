package tech.peller.mrblackandroidwatch.models.reservation;


import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import tech.peller.mrblackandroidwatch.models.seating.StaffAssignment;

/**
 * Created by Sam (samir@peller.tech) on 27.05.2016
 */
public class TableWithSeating extends RealmObject {
    private TableInfo tableInfo;
    private ReservationInfo reservationInfo;
    private RealmList<ReservationInfo> reservations;
    private TableInfo combinedWith;
    private RealmList<TableInfo> attachedTables;
    private RealmList<StaffAssignment> staff;
    private boolean isCopiedFromMultiRes;
    private boolean isOnCombinedTable;

    public TableWithSeating() {}

    public TableWithSeating(TableInfo tableInfo, ReservationInfo reservationInfo) {
        this.tableInfo = tableInfo;
        this.reservationInfo = reservationInfo;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    public ReservationInfo getReservationInfo() {
        return reservationInfo;
    }

    public void setReservationInfo(ReservationInfo reservationInfo) {
        this.reservationInfo = reservationInfo;
    }

    public RealmList<ReservationInfo> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationInfo> reservationslist) {
        RealmList<ReservationInfo> reservations = new RealmList<>();
        reservations.addAll(reservationslist);
        this.reservations = reservations;
    }

    public TableInfo getCombinedWith() {
        return combinedWith;
    }

    public void setCombinedWith(TableInfo combinedWith) {
        this.combinedWith = combinedWith;
    }

    public List<TableInfo> getAttachedTables() {
        return attachedTables;
    }

    public void setAttachedTables(List<TableInfo> attachedTablesList) {
        RealmList<TableInfo> attachedTables = new RealmList<>();
        attachedTables.addAll(attachedTablesList);
        this.attachedTables = attachedTables;
    }

    public List<StaffAssignment> getStaff() {
        return staff;
    }

    public void setStaff(List<StaffAssignment> staffList) {
        RealmList<StaffAssignment> staff = new RealmList<>();
        staff.addAll(staffList);
        this.staff = staff;
    }

    public boolean isCopiedFromMultiRes() {
        return isCopiedFromMultiRes;
    }

    public void setCopiedFromMultiRes(boolean copiedFromMultiRes) {
        isCopiedFromMultiRes = copiedFromMultiRes;
    }

    public boolean isOnCombinedTable() {
        return isOnCombinedTable;
    }

    public void setOnCombinedTable(boolean onCombinedTable) {
        isOnCombinedTable = onCombinedTable;
    }
}
