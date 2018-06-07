package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.DbJsonB;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by arkady on 04/03/16.
 */
@Entity
public class Reservation extends Model {

	@Id
	private Long id;
	private String guestFullName;
	private UUID token;

	@ManyToOne
	@JoinColumn(name = "guest_id", referencedColumnName = "id")
	private User guest;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "completed_by_id", referencedColumnName = "id")
	private User completedBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "confirmed_by_id", referencedColumnName = "id")
	private User confirmedBy;

	@Column(columnDefinition = "varchar(500)")
	private String bookingNote;
	@Column(columnDefinition = "smallint default 0")
	private short guestsNumber;
	@Column(columnDefinition = "smallint default 0")
	private Short guysSeated;
	@Column(columnDefinition = "smallint default 0")
	private Short girlsSeated;
	private Boolean notifyMgmtOnArrival;

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
	@Column(columnDefinition = "boolean default false")
	private Boolean deleted;

	@Enumerated(EnumType.STRING)
	private GroupType groupType;

	private Short bottleMin;
	@Column(columnDefinition = "integer default 0")
	private Integer minSpend = 0;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<ReservationUser> staff = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Picture> photos = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "place_id", referencedColumnName = "id")
	private Place table;

	@ManyToOne
	@JoinColumn(name = "booked_by_id", referencedColumnName = "id", nullable = false, updatable = false)
	private User bookedBy;

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "payee", cascade = CascadeType.REMOVE)
	private List<PayInfo> payees = new ArrayList<>();

	@DbJsonB
	private JsonNode tags;

	private LocalDate creationDate;
	private LocalTime creationTime;

	@Enumerated(EnumType.STRING)
	private BottleServiceType bottleService;

	private Integer totalSpent;

	private LocalTime arrivalTime;
	private LocalTime estimatedArrivalTime;
	private LocalTime statusChangeTime;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Feedback> feedbackList = new ArrayList<>();

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "completion_feedback_id", referencedColumnName = "id")
	private Feedback completionFeedback;

	@Enumerated(EnumType.STRING)
	private ReservationStatus status;

	@ManyToOne(fetch = FetchType.LAZY, cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE,
			CascadeType.REFRESH
	}, optional = false)
	private EventInstance eventInstance;

	@ManyToOne
	@JoinColumn(name = "released_from_place_id", referencedColumnName = "id")
	private Place releasedFrom;

	public static Finder<Long, Reservation> find = new Finder<>(Reservation.class);

	public Reservation() {
	}

	public UUID getToken() {
		return token;
	}

	public void setToken(UUID token) {
		this.token = token;
	}

	public Reservation(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGuestFullName() {
		return guestFullName;
	}

	public void setGuestFullName(String guestFullName) {
		this.guestFullName = guestFullName;
	}

	public short getGuestsNumber() {
		return guestsNumber;
	}

	public void setGuestsNumber(short guestsNumber) {
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

	public Short getComplimentGuysQty() {
		return complimentGuysQty;
	}

	public void setComplimentGuysQty(Short complimentGuysQty) {
		this.complimentGuysQty = complimentGuysQty;
	}

	public Short getComplimentGirlsQty() {
		return complimentGirlsQty;
	}

	public void setComplimentGirlsQty(Short complimentGirlsQty) {
		this.complimentGirlsQty = complimentGirlsQty;
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
		return minSpend != null ? minSpend : 0; //TODO add constraint and delete this check
	}

	public void setMinSpend(Integer minSpend) {
		this.minSpend = minSpend;
	}

	public Place getTable() {
		return table;
	}

	public void setTable(Place table) {
		this.table = table;
	}

	public User getBookedBy() {
		return bookedBy;
	}

	public void setBookedBy(User bookedBy) {
		this.bookedBy = bookedBy;
	}

	public BottleServiceType getBottleService() {
		return bottleService;
	}

	public void setBottleService(BottleServiceType bottleService) {
		this.bottleService = bottleService;
	}

	public Integer getTotalSpent() {
		return totalSpent != null ? totalSpent : 0;
	}

	public void setTotalSpent(Integer totalSpent) {
		this.totalSpent = totalSpent;
	}

	public String getBookingNote() {
		return bookingNote;
	}

	public void setBookingNote(String bookingNote) {
		this.bookingNote = bookingNote;
	}

	public User getGuest() {
		return guest;
	}

	public void setGuest(User guest) {
		this.guest = guest;
	}

	public User getCompletedBy() {
		return completedBy;
	}

	public void setCompletedBy(User completedBy) {
		this.completedBy = completedBy;
	}

	public User getConfirmedBy() {
		return confirmedBy;
	}

	public void setConfirmedBy(User confirmedBy) {
		this.confirmedBy = confirmedBy;
	}

	public ReservationStatus getStatus() {
		return status;
	}

	public void setStatus(ReservationStatus status) {
		this.status = status;
	}

	public LocalTime getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(LocalTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public LocalTime getEstimatedArrivalTime() {
		return estimatedArrivalTime;
	}

	public void setEstimatedArrivalTime(LocalTime estimatedArrivalTime) {
		this.estimatedArrivalTime = estimatedArrivalTime;
	}

	public LocalTime getStatusChangeTime() {
		return statusChangeTime;
	}

	public void setStatusChangeTime(LocalTime statusChangeTime) {
		this.statusChangeTime = statusChangeTime;
	}

	public List<Feedback> getFeedbackList() {
		return feedbackList;
	}

	public void setFeedbackList(List<Feedback> feedbackList) {
		this.feedbackList = feedbackList;
	}

	public EventInstance getEventInstance() {
		return eventInstance;
	}

	public void setEventInstance(EventInstance eventInstance) {
		this.eventInstance = eventInstance;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public List<ReservationUser> getStaff() {
		return staff;
	}

	public void setStaff(List<ReservationUser> staff) {
		this.staff = staff;
	}

	public Short getGuysSeated() {
		return guysSeated != null ? guysSeated : 0;
	}

	public void setGuysSeated(Short guysSeated) {
		this.guysSeated = guysSeated;
	}

	public Short getGirlsSeated() {
		return girlsSeated != null ? girlsSeated : 0;
	}

	public void setGirlsSeated(Short girlsSeated) {
		this.girlsSeated = girlsSeated;
	}

	public List<String> getTags() {
		if (tags != null) {
			final List<String> tagList = Arrays.asList(Json.fromJson(tags, String[].class));
			return new ArrayList<>(tagList);
		} else {
			return new ArrayList<>();
		}
	}

	public void setTags(List<String> tags) {
		this.tags = Json.toJson(tags);
	}

	public List<Picture> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Picture> photos) {
		this.photos = photos;
	}

	public List<PayInfo> getPayees() {
		return payees;
	}

	public void setPayees(List<PayInfo> payees) {
		this.payees = payees;
	}

	public Place getReleasedFrom() {
		return releasedFrom;
	}

	public void setReleasedFrom(Place releasedFrom) {
		this.releasedFrom = releasedFrom;
	}

	public Feedback getCompletionFeedback() {
		return completionFeedback;
	}

	public void setCompletionFeedback(Feedback completionFeedback) {
		this.completionFeedback = completionFeedback;
	}

	public LocalDate getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDate creationDate) {
		this.creationDate = creationDate;
	}

	public LocalTime getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(LocalTime creationTime) {
		this.creationTime = creationTime;
	}

	@Override
	public String toString() {
		return "Reservation{" +
				"id=" + id +
				", guestFullName='" + guestFullName + '\'' +
				", guest=" + guest +
				", bookingNote='" + bookingNote + '\'' +
				", guestsNumber=" + guestsNumber +
				", notifyMgmtOnArrival=" + notifyMgmtOnArrival +
				", complimentGirls=" + complimentGirls +
				", complimentGuys=" + complimentGuys +
				", complimentGuysQty=" + complimentGuysQty +
				", complimentGirlsQty=" + complimentGirlsQty +
				", reducedGirls=" + reducedGirls +
				", reducedGuys=" + reducedGuys +
				", reducedGuysQty=" + reducedGuysQty +
				", reducedGirlsQty=" + reducedGirlsQty +
				", mustEnter=" + mustEnter +
				", deleted=" + deleted +
				", groupType=" + groupType +
				", bottleMin=" + bottleMin +
				", minSpend=" + minSpend +
				", table=" + table +
				", bookedBy=" + bookedBy +
				", bottleService=" + bottleService +
				", totalSpent=" + totalSpent +
				", arrivalTime=" + arrivalTime +
				", creationDate=" + creationDate +
				", creationTime=" + creationTime +
				", feedbackList=" + feedbackList +
				", status=" + status +
				", girlsSeated=" + girlsSeated +
				", guysSeated=" + guysSeated +
				", eventInstance=" + eventInstance +
				", releasedFrom=" + releasedFrom +
				", tags=" + tags +
				'}';
	}
}