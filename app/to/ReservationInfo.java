package to;

import io.swagger.annotations.ApiModelProperty;
import models.BottleServiceType;
import models.GroupType;
import models.ReservationStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkady on 07/03/16.
 */
public class ReservationInfo {
	@ApiModelProperty(readOnly = true)
	private Long id;

	@ApiModelProperty(required = true)
	private VisitorInfo guestInfo;
	private PayInfoTO guestContactInfo;

	@ApiModelProperty(value = "Guests who will come with reservation owner")
	private Short guestsNumber;

	@ApiModelProperty(value = "Total guests number (guestsNumber + 1)", readOnly = true)
	private short totalGuests;

	@ApiModelProperty(readOnly = true)
	private short arrivedGuests;
	@ApiModelProperty(readOnly = true)
	private short arrivedGirls;
	@ApiModelProperty(readOnly = true)
	private short arrivedGuys;
	private Boolean notifyMgmtOnArrival;
	private String bookingNote;
	private Boolean complimentGirls;
	private Boolean complimentGuys;
	private Short complimentGuysQty;
	private Short complimentGirlsQty;
	private Short complimentGroupQty;
	private Boolean reducedGirls;
	private Boolean reducedGuys;
	private Short reducedGuysQty;
	private Short reducedGirlsQty;
	private Short reducedGroupQty;
	private Boolean mustEnter;
	private GroupType groupType;
	private Short bottleMin;
	private Integer minSpend;
	private ReservationStatus status;
	private Long eventId;
	private List<String> tags;
	private List<String> photos = new ArrayList<>();
	private List<StaffAssignment> staff;

	private TableInfo tableInfo;

	@ApiModelProperty(readOnly = true)
	private UserInfo bookedBy;

	private BottleServiceType bottleService;
	private Integer totalSpent;
	private float rating;

	@ApiModelProperty(required = true)
	private Long venueId;

	@ApiModelProperty(required = true, example = "2016-10-02")
	private String reservationDate;

	@ApiModelProperty(readOnly = true)
	private int feedbackCount;

	@ApiModelProperty(readOnly = true)
	private String arrivalTime;
	private String estimatedArrivalTime;
	private String statusChangeTime;
	@ApiModelProperty(readOnly = true)
	private String creationDate;
	@ApiModelProperty(readOnly = true)
	private String creationTime;
	private Boolean deleted;

	private List<PayInfoTO> payees;
	private FeedbackInfo completionFeedback;

	public List<String> getPhotos() {
		return photos;
	}

	public void setPhotos(List<String> photos) {
		this.photos = photos;
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

	public Short getGuestsNumber() {
		return guestsNumber;
	}

	public void setGuestsNumber(Short guestsNumber) {
		this.guestsNumber = guestsNumber;
	}

	public Boolean getNotifyMgmtOnArrival() {
		return notifyMgmtOnArrival;
	}

	public void setNotifyMgmtOnArrival(Boolean notifyMgmtOnArrival) {
		this.notifyMgmtOnArrival = notifyMgmtOnArrival;
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

	public Boolean getReducedGuys() {
		return reducedGuys;
	}

	public void setReducedGuys(Boolean reducedGuys) {
		this.reducedGuys = reducedGuys;
	}

	public Short getReducedGuysQty() {
		return reducedGuysQty;
	}

	public void setReducedGuysQty(Short reducedGuysQty) {
		this.reducedGuysQty = reducedGuysQty;
	}

	public Short getReducedGirlsQty() {
		return reducedGirlsQty;
	}

	public void setReducedGirlsQty(Short reducedGirlsQty) {
		this.reducedGirlsQty = reducedGirlsQty;
	}

	public Short getComplimentGroupQty() {
		return complimentGroupQty;
	}

	public void setComplimentGroupQty(Short complimentGroupQty) {
		this.complimentGroupQty = complimentGroupQty;
	}

	public Short getReducedGroupQty() {
		return reducedGroupQty;
	}

	public void setReducedGroupQty(Short reducedGroupQty) {
		this.reducedGroupQty = reducedGroupQty;
	}

	public Short getComplimentGirlsQty() {
		return complimentGirlsQty;
	}

	public void setComplimentGirlsQty(Short complimentGirlsQty) {
		this.complimentGirlsQty = complimentGirlsQty;
	}

	public Short getComplimentGuysQty() {
		return complimentGuysQty;
	}

	public void setComplimentGuysQty(Short complimentGuysQty) {
		this.complimentGuysQty = complimentGuysQty;
	}

	public Boolean getReducedGirls() {
		return reducedGirls;
	}

	public void setReducedGirls(Boolean reducedGirls) {
		this.reducedGirls = reducedGirls;
	}

	public Boolean getMustEnter() {
		return mustEnter;
	}

	public void setMustEnter(Boolean mustEnter) {
		this.mustEnter = mustEnter;
	}

	public GroupType getGroupType() {
		return groupType;
	}

	public void setGroupType(GroupType groupType) {
		this.groupType = groupType;
	}

	public Short getBottleMin() {
		return bottleMin;
	}

	public void setBottleMin(Short bottleMin) {
		this.bottleMin = bottleMin;
	}

	public Integer getMinSpend() {
		return minSpend;
	}

	public void setMinSpend(Integer minSpend) {
		this.minSpend = minSpend;
	}

	public TableInfo getTableInfo() {
		return tableInfo;
	}

	public void setTableInfo(TableInfo tableId) {
		this.tableInfo = tableId;
	}

	public UserInfo getBookedBy() {
		return bookedBy;
	}

	public void setBookedBy(UserInfo bookedBy) {
		this.bookedBy = bookedBy;
	}

	public BottleServiceType getBottleService() {
		return bottleService;
	}

	public void setBottleService(BottleServiceType bottleService) {
		this.bottleService = bottleService;
	}

	public Integer getTotalSpent() {
		return totalSpent;
	}

	public void setTotalSpent(Integer totalSpent) {
		this.totalSpent = totalSpent;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
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

	public String getBookingNote() {
		return bookingNote;
	}

	public void setBookingNote(String bookingNote) {
		this.bookingNote = bookingNote;
	}

	public void setFeedbackCount(int feedbackCount) {
		this.feedbackCount = feedbackCount;
	}

	public int getFeedbackCount() {
		return feedbackCount;
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

	public short getTotalGuests() {
		return totalGuests;
	}

	public void setTotalGuests(short totalGuests) {
		this.totalGuests = totalGuests;
	}

	public short getArrivedGuests() {
		return arrivedGuests;
	}

	public void setArrivedGuests(short arrivedGuests) {
		this.arrivedGuests = arrivedGuests;
	}

	public short getArrivedGirls() {
		return arrivedGirls;
	}

	public void setArrivedGirls(short arrivedGirls) {
		this.arrivedGirls = arrivedGirls;
	}

	public short getArrivedGuys() {
		return arrivedGuys;
	}

	public void setArrivedGuys(short arrivedGuys) {
		this.arrivedGuys = arrivedGuys;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public ReservationStatus getStatus() {
		return status;
	}

	public void setStatus(ReservationStatus status) {
		this.status = status;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<StaffAssignment> getStaff() {
		return staff;
	}

	public void setStaff(List<StaffAssignment> staff) {
		this.staff = staff;
	}

	public List<PayInfoTO> getPayees() {
		return payees;
	}

	public void setPayees(List<PayInfoTO> payees) {
		this.payees = payees;
	}

	public FeedbackInfo getCompletionFeedback() {
		return completionFeedback;
	}

	public void setCompletionFeedback(FeedbackInfo completionFeedback) {
		this.completionFeedback = completionFeedback;
	}

	public PayInfoTO getGuestContactInfo() {
		return guestContactInfo;
	}

	public void setGuestContactInfo(PayInfoTO guestContactInfo) {
		this.guestContactInfo = guestContactInfo;
	}
}
