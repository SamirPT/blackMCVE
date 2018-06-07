package tech.peller.mrblackandroidwatch.models.reservation;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import tech.peller.mrblackandroidwatch.enums.BottleServiceTypeEnum;
import tech.peller.mrblackandroidwatch.enums.GroupType;
import tech.peller.mrblackandroidwatch.enums.PaymentMethodEnum;
import tech.peller.mrblackandroidwatch.enums.ReservationStatus;
import tech.peller.mrblackandroidwatch.models.seating.StaffAssignment;
import tech.peller.mrblackandroidwatch.models.user.UserInfo;
import tech.peller.mrblackandroidwatch.models.venue.Venue;


public class ReservationInfo extends RealmObject implements Serializable, Parcelable {

    public static final Creator<ReservationInfo> CREATOR = new Creator<ReservationInfo>() {
        @Override
        public ReservationInfo createFromParcel(Parcel in) {
            return new ReservationInfo(in);
        }

        @Override
        public ReservationInfo[] newArray(int size) {
            return new ReservationInfo[size];
        }
    };
    private Long id;
    private VisitorInfo guestInfo;
    private PayInfoTO guestContactInfo;
    private Integer guestsNumber;
    private Integer totalGuests;
    private Integer arrivedGuests;
    private Integer arrivedGirls;
    private Integer arrivedGuys;
    private Boolean notifyMgmtOnArrival = false;
    private String bookingNote;
    private Boolean complimentGirls = false;
    private Boolean complimentGuys = false;
    private Integer complimentGuysQty;
    private Integer complimentGirlsQty;
    private Integer complimentGroupQty;
    private Boolean fromWeb;
    private Boolean reducedGirls = false;
    private Boolean reducedGuys = false;
    private Integer reducedGuysQty;
    private Integer reducedGirlsQty;
    private Integer reducedGroupQty;
    private Boolean mustEnter = false;
    private String groupType;
    private String paymentMethod;
    private Integer bottleMin;
    private Integer minSpend;
    private Integer signedBottleMin = null;
    private Integer signedMinSpend = null;
    private String status;
    private String previousStatus;
    private Long eventId;
    private RealmList<String> tags = new RealmList<>();
    private RealmList<String> photos = new RealmList<>();
    private RealmList<SignatureTO> signatures = new RealmList<>();
    private RealmList<StaffAssignment> staff = new RealmList<>();
    private TableInfo tableInfo;
    private UserInfo bookedBy;
    private UserInfo rejectedBy;
    private UserInfo cancelledBy;
    private String bottleService;
    private Integer totalSpent;
    private Float rating;
    private Long venueId;
    private String reservationDate;
    private Integer feedbackCount;
    private String arrivalTime;
    private String estimatedArrivalTime;
    private String statusChangeTime;
    private String lastChangeDate;
    private String lastChangeTime;
    private String creationDate;
    private String creationTime;
    private RealmList<TableInfo> attachedTables;
    private Boolean deleted = false;
    private RealmList<PayInfoTO> payees = new RealmList<>();
    private FeedbackInfo completionFeedback;
    private Venue bookedByPromoter;
    private String cancellationMessage;
    private String rejectionMessage;
    private Integer tablesRequired;
    private Boolean fromLinkedVenue;
    private Integer liveSpend;


    protected ReservationInfo(Parcel in) {
        bookingNote = in.readString();
        groupType = in.readString();
        status = in.readString();
        bottleService = in.readString();
        reservationDate = in.readString();
        arrivalTime = in.readString();
        estimatedArrivalTime = in.readString();
        statusChangeTime = in.readString();
        creationDate = in.readString();
        creationTime = in.readString();
        cancellationMessage = in.readString();
    }

    public ReservationInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VisitorInfo getGuestInfo() {
        return guestInfo;
    }

    public void setGuestInfo(VisitorInfo guestInfo) {
        this.guestInfo = guestInfo;
    }

    public PayInfoTO getGuestContactInfo() {
        return guestContactInfo;
    }

    public void setGuestContactInfo(PayInfoTO guestContactInfo) {
        this.guestContactInfo = guestContactInfo;
    }

    public Boolean getFromWeb() {
        return fromWeb;
    }

    public void setFromWeb(Boolean fromWeb) {
        this.fromWeb = fromWeb;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public void setTags(RealmList<String> tags) {
        this.tags = tags;
    }

    public void setPhotos(RealmList<String> photos) {
        this.photos = photos;
    }

    public void setSignatures(RealmList<SignatureTO> signatures) {
        this.signatures = signatures;
    }

    public void setStaff(RealmList<StaffAssignment> staff) {
        this.staff = staff;
    }

    public void setAttachedTables(RealmList<TableInfo> attachedTables) {
        this.attachedTables = attachedTables;
    }

    public void setPayees(RealmList<PayInfoTO> payees) {
        this.payees = payees;
    }

    public Integer getGuestsNumber() {
        return guestsNumber;
    }

    public void setGuestsNumber(Integer guestsNumber) {
        this.guestsNumber = guestsNumber;
    }

    public Integer getTotalGuests() {
        return totalGuests;
    }

    public void setTotalGuests(Integer totalGuests) {
        this.totalGuests = totalGuests;
    }

    public Integer getArrivedGuests() {
        return arrivedGuests;
    }

    public void setArrivedGuests(Integer arrivedGuests) {
        this.arrivedGuests = arrivedGuests;
    }

    public Integer getArrivedGirls() {
        return arrivedGirls;
    }

    public void setArrivedGirls(Integer arrivedGirls) {
        this.arrivedGirls = arrivedGirls;
    }

    public Integer getArrivedGuys() {
        return arrivedGuys;
    }

    public void setArrivedGuys(Integer arrivedGuys) {
        this.arrivedGuys = arrivedGuys;
    }

    public Boolean getNotifyMgmtOnArrival() {
        return notifyMgmtOnArrival;
    }

    public void setNotifyMgmtOnArrival(Boolean notifyMgmtOnArrival) {
        this.notifyMgmtOnArrival = notifyMgmtOnArrival;
    }

    public String getBookingNote() {
        return bookingNote;
    }

    public void setBookingNote(String bookingNote) {
        this.bookingNote = bookingNote;
    }

    public Boolean getComplimentGirls() {
        return complimentGirls;
    }

    public void setComplimentGirls(Boolean complimentGirls) {
        this.complimentGirls = complimentGirls;
    }

    public Boolean getComplimentGuys() {
        return complimentGuys;
    }

    public void setComplimentGuys(Boolean complimentGuys) {
        this.complimentGuys = complimentGuys;
    }

    public Integer getComplimentGuysQty() {
        return complimentGuysQty;
    }

    public void setComplimentGuysQty(Integer complimentGuysQty) {
        this.complimentGuysQty = complimentGuysQty;
    }

    public Integer getComplimentGirlsQty() {
        return complimentGirlsQty;
    }

    public void setComplimentGirlsQty(Integer complimentGirlsQty) {
        this.complimentGirlsQty = complimentGirlsQty;
    }

    public Boolean getReducedGirls() {
        return reducedGirls;
    }

    public void setReducedGirls(Boolean reducedGirls) {
        this.reducedGirls = reducedGirls;
    }

    public Boolean getReducedGuys() {
        return reducedGuys;
    }

    public void setReducedGuys(Boolean reducedGuys) {
        this.reducedGuys = reducedGuys;
    }

    public Integer getReducedGuysQty() {
        return reducedGuysQty;
    }

    public void setReducedGuysQty(Integer reducedGuysQty) {
        this.reducedGuysQty = reducedGuysQty;
    }

    public Integer getReducedGirlsQty() {
        return reducedGirlsQty;
    }

    public void setReducedGirlsQty(Integer reducedGirlsQty) {
        this.reducedGirlsQty = reducedGirlsQty;
    }

    public Integer getComplimentGroupQty() {
        return complimentGroupQty;
    }

    public void setComplimentGroupQty(Integer complimentGroupQty) {
        this.complimentGroupQty = complimentGroupQty;
    }

    public Integer getReducedGroupQty() {
        return reducedGroupQty;
    }

    public void setReducedGroupQty(Integer reducedGroupQty) {
        this.reducedGroupQty = reducedGroupQty;
    }

    public Boolean getMustEnter() {
        return mustEnter;
    }

    public void setMustEnter(Boolean mustEnter) {
        this.mustEnter = mustEnter;
    }

    public GroupType getGroupType() {
        if (this.groupType != null) {
            return GroupType.valueOf(this.groupType);
        } else return null;

    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType.name();
    }

    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod != null ? PaymentMethodEnum.valueOf(paymentMethod) :
                PaymentMethodEnum.NONE;
    }

    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod.name();
    }

    public Integer getBottleMin() {
        return bottleMin;
    }

    public void setBottleMin(Integer bottleMin) {
        this.bottleMin = bottleMin;
    }

    public Integer getMinSpend() {
        return minSpend;
    }

    public void setMinSpend(Integer minSpend) {
        this.minSpend = minSpend;
    }

    public Integer getSignedBottleMin() {
        return signedBottleMin;
    }

    public void setSignedBottleMin(Integer signedBottleMin) {
        this.signedBottleMin = signedBottleMin;
    }

    public Integer getSignedMinSpend() {
        return signedMinSpend;
    }

    public void setSignedMinSpend(Integer signedMinSpend) {
        this.signedMinSpend = signedMinSpend;
    }

    public ReservationStatus getStatus() {
        if (this.status != null) {
            return ReservationStatus.valueOf(this.status);
        } else return null;
    }

    public void setStatus(ReservationStatus status) {
        if (status == null) return;
        this.status = status.name();
    }

    public ReservationStatus getPreviousStatus() {
        return previousStatus != null ? ReservationStatus.valueOf(previousStatus) : null;
    }

    public void setPreviousStatus(ReservationStatus previousStatus) {
        if (previousStatus == null) return;
        this.previousStatus = previousStatus.name();
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public List<String> getTags() {
        ArrayList<String> tagsList = new ArrayList<>();
        if (!this.tags.isEmpty()) {
            tagsList.addAll(this.tags);
        }
        return tagsList;
    }

    public void setTags(List<String> tags) {
        RealmList<String> tagsList = new RealmList<>();
        tagsList.addAll(tags);
        this.tags = tagsList;
    }

    public List<String> getPhotos() {
        ArrayList<String> photosList = new ArrayList<>();
        if (!this.photos.isEmpty()) {
            photosList.addAll(this.photos);
        }
        return photosList;
    }

    public void setPhotos(List<String> photos) {
        RealmList<String> photoList = new RealmList<>();
        photoList.addAll(photos);
        this.photos = photoList;
    }

    public void addPhoto(String photo) {
        this.photos.add(photo);
    }

    public List<SignatureTO> getSignatures() {
        return signatures;
    }

    public void setSignatures(List<SignatureTO> signatures) {
        RealmList<SignatureTO> signaturesRL = new RealmList<>();
        signaturesRL.addAll(signatures);
        this.signatures = signaturesRL;
    }

    public List<StaffAssignment> getStaff() {
        return staff;
    }

    public void setStaff(List<StaffAssignment> staff) {
        RealmList<StaffAssignment> staffAssignments = new RealmList<>();
        staffAssignments.addAll(staff);
        this.staff = staffAssignments;
    }

    public BottleServiceTypeEnum getBottleService() {
        if (this.bottleService != null) {
            return BottleServiceTypeEnum.valueOf(this.bottleService);
        } else return null;

    }

    public void setBottleService(String bottleService) {
        this.bottleService = bottleService;
    }

    public void setBottleService(BottleServiceTypeEnum groupType) {
        this.bottleService = groupType.name();
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    public UserInfo getBookedBy() {
        return bookedBy;
    }

    public void setBookedBy(UserInfo bookedBy) {
        this.bookedBy = bookedBy;
    }

    public UserInfo getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(UserInfo rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public UserInfo getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(UserInfo cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public Integer getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(Integer totalSpent) {
        this.totalSpent = totalSpent;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public Integer getFeedbackCount() {
        return feedbackCount;
    }

    public void setFeedbackCount(Integer feedbackCount) {
        this.feedbackCount = feedbackCount;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public void setEstimatedArrivalTime(String estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    public String getStatusChangeTime() {
        return statusChangeTime;
    }

    public void setStatusChangeTime(String statusChangeTime) {
        this.statusChangeTime = statusChangeTime;
    }

    public String getLastChangeDate() {
        return lastChangeDate;
    }

    public void setLastChangeDate(String lastChangeDate) {
        this.lastChangeDate = lastChangeDate;
    }

    public String getLastChangeTime() {
        return lastChangeTime;
    }

    public void setLastChangeTime(String lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public List<TableInfo> getAttachedTables() {
        return attachedTables;
    }

    public void setAttachedTables(List<TableInfo> attachedTablesList) {
        RealmList<TableInfo> attachedTables = new RealmList<>();
        for (TableInfo element : attachedTablesList) attachedTables.add(element);
        this.attachedTables = attachedTables;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public List<PayInfoTO> getPayees() {
        return payees;
    }

    public void setPayees(List<PayInfoTO> payees) {
        if (payees == null) return;
        RealmList<PayInfoTO> payeesList = new RealmList<>();
        for (PayInfoTO payInfoTO : payees) payeesList.add(payInfoTO);
        this.payees = payeesList;
    }

    public FeedbackInfo getCompletionFeedback() {
        return completionFeedback;
    }

    public void setCompletionFeedback(FeedbackInfo completionFeedback) {
        this.completionFeedback = completionFeedback;
    }

    public Venue getBookedByPromoter() {
        return bookedByPromoter;
    }

    public void setBookedByPromoter(Venue bookedByPromoter) {
        this.bookedByPromoter = bookedByPromoter;
    }

    public String getCancellationMessage() {
        return cancellationMessage;
    }

    public void setCancellationMessage(String cancellationMessage) {
        this.cancellationMessage = cancellationMessage;
    }

    public Integer getTablesRequired() {
        return tablesRequired;
    }

    public void setTablesRequired(Integer tablesRequired) {
        this.tablesRequired = tablesRequired;
    }

    public Integer getLiveSpend() {
        return liveSpend;
    }

    public void setLiveSpend(Integer liveSpend) {
        this.liveSpend = liveSpend;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookingNote);
        dest.writeString(groupType);
        dest.writeString(status);
        dest.writeString(bottleService);
        dest.writeString(reservationDate);
        dest.writeString(arrivalTime);
        dest.writeString(estimatedArrivalTime);
        dest.writeString(statusChangeTime);
        dest.writeString(creationDate);
        dest.writeString(creationTime);
        dest.writeString(cancellationMessage);
    }
}
