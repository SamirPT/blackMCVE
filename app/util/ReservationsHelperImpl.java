package util;

import com.avaje.ebean.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.zxing.WriterException;
import models.*;
import modules.twilio.Twilio;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import play.Logger;
import play.Play;
import play.libs.Json;
import to.*;
import to.guest.GuestReservationTicket;
import to.venue.EODReservationInfo;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by arkady on 08/03/16.
 */
@Singleton
public class ReservationsHelperImpl implements ReservationsHelper {

	private Twilio twilio;

	@Inject
	public ReservationsHelperImpl(Twilio twilio) {
		this.twilio = twilio;
	}

	@Override
	public ReservationInfo reservationToInfo(Reservation r, boolean detailed) {
		return reservationToInfo(r, detailed, true);
	}

	@Override
	public EODReservationInfo reservationToEODInfo(Reservation r, boolean detailed, boolean withStaff) {
		EODReservationInfo eodReservationInfo = (EODReservationInfo) reservationToInfo(r, detailed, withStaff);

		final User completedBy = r.getCompletedBy();
		final User confirmedBy = r.getConfirmedBy();

		final List<Long> assigneeIds = new ArrayList<>();
		if (completedBy != null) assigneeIds.add(completedBy.getId());
		if (confirmedBy != null) assigneeIds.add(confirmedBy.getId());

		Map<Long, ReservationUser> staffIdToAssignmentMap = new HashMap<>();
		if (!assigneeIds.isEmpty()) {
			final List<ReservationUser> reservationUser = ReservationUser.finder
					.fetch("user")
					.where()
					.eq("reservation.id", r.getId())
					.in("user.id", assigneeIds)
					.findList();
			if (reservationUser != null && !reservationUser.isEmpty()) {
				reservationUser.forEach(assignment -> {
					if (!staffIdToAssignmentMap.containsKey(assignment.getReservation().getId())) {
						staffIdToAssignmentMap.put(assignment.getReservation().getId(), assignment);
					}
				});
			}
		}

		if (completedBy != null) {
			ReservationUser reservationUser = getOrCreateUserAssignment(completedBy, staffIdToAssignmentMap);
			eodReservationInfo.setCompletedBy(new StaffAssignment(reservationUser));
		}

		if (confirmedBy != null) {
			ReservationUser reservationUser = getOrCreateUserAssignment(confirmedBy, staffIdToAssignmentMap);
			eodReservationInfo.setConfirmedBy(new StaffAssignment(reservationUser));
		}
		return eodReservationInfo;
	}

	private ReservationUser getOrCreateUserAssignment(User completedBy, Map<Long, ReservationUser> staffIdToAssignmentMap) {
		ReservationUser reservationUser = null;
		if (staffIdToAssignmentMap != null) {
			reservationUser = staffIdToAssignmentMap.get(completedBy.getId());
		}

		if (reservationUser == null) {
			reservationUser = new ReservationUser();
			reservationUser.setUser(completedBy);
			reservationUser.setPk(new ReservationUserPK());
		}
		return reservationUser;
	}

	@Override
	public boolean isReservationInApprovedList(Reservation r) {
		final List<ReservationStatus> approvedStatuses = Arrays.asList(
				ReservationStatus.APPROVED,
				ReservationStatus.ARRIVED,
				ReservationStatus.CONFIRMED_COMPLETE,
				ReservationStatus.COMPLETED,
				ReservationStatus.NO_SHOW,
				ReservationStatus.PRE_RELEASED,
				ReservationStatus.RELEASED);
		return approvedStatuses.contains(r.getStatus());
	}

	@Override
	public ReservationInfo reservationToInfo(Reservation r, boolean detailed, boolean withStaff) {
		ReservationInfo reservationInfo = new EODReservationInfo();
		VisitorInfo visitorInfo;

		if (detailed && r.getGuest() != null) {
			visitorInfo = getDetailedVisitorInfo(r.getEventInstance().getDateVenuePk().getVenueId(), r.getGuest());

			if (r.getGuest().getPayInfo() != null) {
				reservationInfo.setGuestContactInfo(new PayInfoTO(r.getGuest().getPayInfo()));
			}
		} else {
			visitorInfo = getCommonVisitorInfo(r);
		}

		reservationInfo.setGuestInfo(visitorInfo);
		if (r.getCreationDate() != null) {
			reservationInfo.setCreationDate(r.getCreationDate().toString());
		}

		if (r.getCreationTime() != null) {
			reservationInfo.setCreationTime(r.getCreationTime().toString());
		}

		reservationInfo.setId(r.getId());
		reservationInfo.setDeleted(r.getDeleted());
		reservationInfo.setBookedBy(new UserInfo(r.getBookedBy()));
		reservationInfo.setBookingNote(r.getBookingNote());
		reservationInfo.setComplimentGirls(r.getComplimentGirls());
		reservationInfo.setComplimentGuys(r.getComplimentGuys());
		reservationInfo.setComplimentGuysQty(r.getComplimentGuysQty());
		reservationInfo.setComplimentGirlsQty(r.getComplimentGirlsQty());
		reservationInfo.setComplimentGroupQty(r.getComplimentGroupQty());
		reservationInfo.setGroupType(r.getGroupType());
		reservationInfo.setMustEnter(r.getMustEnter());
		reservationInfo.setReducedGirls(r.getReducedGirls());
		reservationInfo.setReducedGuys(r.getReducedGuys());
		reservationInfo.setReducedGuysQty(r.getReducedGuysQty());
		reservationInfo.setReducedGirlsQty(r.getReducedGirlsQty());
		reservationInfo.setReducedGroupQty(r.getReducedGroupQty());

		final Place table = r.getTable() != null ? r.getTable() : r.getReleasedFrom();
		reservationInfo.setTableInfo(new TableInfo(table));

		reservationInfo.setTotalGuests((short) (r.getGuestsNumber() + 1));
		reservationInfo.setArrivedGuests((short) (r.getGirlsSeated() + r.getGuysSeated()));
		reservationInfo.setArrivedGirls(r.getGirlsSeated());
		reservationInfo.setArrivedGuys(r.getGuysSeated());
		reservationInfo.setBottleService(r.getBottleService());
		reservationInfo.setStatus(r.getStatus());
		reservationInfo.setTags(r.getTags());
		reservationInfo.setEventId(r.getEventInstance().getDateVenuePk().getEventId());
		reservationInfo.setReservationDate(r.getEventInstance().getDate().toString());
		reservationInfo.setMinSpend(r.getMinSpend());
		reservationInfo.setBottleMin(r.getBottleMin());

		if (r.getStaff() != null && withStaff) {
			List<StaffAssignment> staffList = r.getStaff().stream()
					.map(StaffAssignment::new)
					.collect(Collectors.toList());
			reservationInfo.setStaff(staffList);
		}

		if (r.getPhotos() != null && r.getPhotos().size() > 0) {
			reservationInfo.setPhotos(r.getPhotos().stream().map(Picture::getLink).collect(Collectors.toList()));
		}

		if (r.getArrivalTime() != null) {
			LocalDateTime dateTime = LocalDateTime.of(r.getEventInstance().getDate(), r.getArrivalTime());
			reservationInfo.setArrivalTime(dateTime.toString());
		}

		if (r.getEstimatedArrivalTime() != null) {
			reservationInfo.setEstimatedArrivalTime(r.getEstimatedArrivalTime().toString());
		}

		if (detailed) {
			reservationInfo.setGuestsNumber(r.getGuestsNumber());
			reservationInfo.setNotifyMgmtOnArrival(r.getNotifyMgmtOnArrival());
			reservationInfo.setRating(getReservationRating(r.getId()));
			reservationInfo.setTotalSpent(r.getTotalSpent());

			if (r.getStatusChangeTime() != null) {
				reservationInfo.setStatusChangeTime(r.getStatusChangeTime().toString());
			}

			reservationInfo.setVenueId(r.getEventInstance().getDateVenuePk().getVenueId());
			reservationInfo.setCompletionFeedback(new FeedbackInfo(r.getCompletionFeedback()));

			if (r.getPayees() != null && r.getPayees().size() > 0) {
				final List<PayInfoTO> payees = r.getPayees().stream().map(PayInfoTO::new).collect(Collectors.toList());
				reservationInfo.setPayees(payees);
			}
		}

		try {
			final int feedbackCount = Feedback.find.where()
					.eq("reservation_id", r.getId())
					.eq("meta", false)
					.findRowCount();
			reservationInfo.setFeedbackCount(feedbackCount);
		} catch (Exception e) {
			Logger.warn("Can't count number of feedback reservation_id=" + r.getId(), e);
		}

		return reservationInfo;
	}

	@Override
	public List<Reservation> getReservationsWithBS(Long venueId, LocalDate date, BottleServiceType bottleServiceType, Long eventId) {
		final List<Reservation> reservations = Ebean.find(Reservation.class)
				.fetch("guest")
				.where()
				.eq("venue_id", venueId)
				.eq("event_id", eventId)
				.eq("date", date)
				.isNull("table")
				.eq("bottleService", bottleServiceType.name())
				.findList();
		return reservations;
	}

	@Override
	public void notifyOnArrival(Reservation reservation, String venueName) {
		if (Boolean.TRUE.equals(reservation.getNotifyMgmtOnArrival())) {
			try {
				final User guest = reservation.getGuest();
				String guestName = guest != null ? guest.getFullName() : reservation.getGuestFullName();
				twilio.sendSMS(reservation.getBookedBy().getPhoneNumber(),
						"The reservation for " + guestName + " at " + venueName + " has been marked as arrived"
				);
			} catch (Exception e) {
				Logger.warn("Can't send SMS about reservation arrival", e);
			}
		}
	}

	@Override
	public void notifyGuestBySMS(Reservation reservation) {
		final String host = Play.application().configuration().getString("app.host");
		if (StringUtils.isEmpty(host)) {
			Logger.warn("Application host not defined in properties!");
			return;
		}
		if (reservation.getGuest() != null) {
			User guest = reservation.getGuest();
			if (StringUtils.isEmpty(guest.getPhoneNumber())) {
				guest = User.finder.byId(guest.getId());
				if (guest == null) return;
			}
			try {
				Logger.debug("host:" + host + ", twilio:" + twilio + ", guest:" + reservation.getGuest());
				twilio.sendSMS(guest.getPhoneNumber(), "Your reservation has been approved! " +
						host + "/reservation/" + reservation.getToken());
			} catch (Exception e) {
				Logger.warn("Can't send reservation info at phone=" + guest.getPhoneNumber(), e);
			}
		}
	}

	@Override
	public List<Reservation> getReservationsWithBSAndTables(Long venueId, LocalDate date, Long eventId) {
		return Ebean.find(Reservation.class)
				.fetch("staff").fetch("staff.user")
				.fetch("guest")
				.where()
				.eq("venue_id", venueId)
				.eq("date", date)
				.eq("event_id", eventId)
				.isNotNull("bottleService").isNotNull("place_id")
				.findList();
	}

	@Override
	public float getReservationRating(Long reservationId) {
		String sql = "select avg(stars) as average from feedback where reservation_id=:reservation_id and stars is not null;";
		SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
		sqlQuery.setParameter("reservation_id", reservationId);

		float result = 0;
		try {
			Float average = sqlQuery.findUnique().getFloat("average");
			if (average != null) {
				result = MathUtil.round(average, 1);
			}
		} catch (Exception e) {
			Logger.warn("Can't get reservation rating for reservationId=" + reservationId, e);
		}

		return result;
	}

	@Override
	public GuestReservationTicket getGuestReservationTicket(String token) {
		Reservation reservation = Ebean.find(Reservation.class)
				.fetch("eventInstance")
				.fetch("eventInstance.venue")
				.where()
				.eq("token", UUID.fromString(token)).findUnique();
		return getGuestReservationTicket(reservation);
	}

	@Override
	public GuestReservationTicket getGuestReservationTicket(Long id) {
		Reservation reservation = getReservationWithFetchedVenue(id);
		return getGuestReservationTicket(reservation);
	}

	private GuestReservationTicket getGuestReservationTicket(Reservation reservation) {
		if (reservation == null) {
			return null;
		}

		GuestReservationTicket ticket = new GuestReservationTicket();
		final EventInstance eventInstance = reservation.getEventInstance();
		ticket.setVenueName(eventInstance.getVenue().getName());
		ticket.setEventName(eventInstance.getEvent().getName());
		ticket.setEventStartsAt(eventInstance.getEvent().getStartsAt().toString());
		ticket.setEventImage(eventInstance.getEvent().getPictureUrl());
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE MMM d, YYYY");
		final String date = eventInstance.getDate().format(formatter);
		ticket.setDate(date);
		ticket.setRawDate(eventInstance.getDate().toString());
		ticket.setBottleMin(reservation.getBottleMin());
		ticket.setMinSpend(reservation.getMinSpend());
		final User guest = reservation.getGuest();
		String guestUserpic = null;
		String guestName;
		if (guest != null) {
			guestName = guest.getFullName();
			guestUserpic = guest.getUserpic();
			ticket.setGuestId(guest.getId());
		} else {
			guestName = reservation.getGuestFullName();
		}
		ticket.setGuestName(guestName);
		ticket.setGuestUserpic(guestUserpic);

		String bookedBy = reservation.getBookedBy().getFullName();
		ticket.setBookedBy(bookedBy);

		String qrTicket = "";
		try {
			qrTicket = generateQrCode(reservation).replace("\r\n","");
		} catch (IOException | WriterException e) {
			Logger.warn("Can't generate QR code for reservation", e);
		}
		ticket.setQrTicket(qrTicket);

		final Boolean hasBottleService = (reservation.getBottleService() != null);
		ticket.setHasBottleService(hasBottleService);

		final short guestsNumber = reservation.getGuestsNumber();
		ticket.setGuestsNumber(guestsNumber);

		return ticket;
	}

	@Override
	public Reservation getReservationWithFetchedVenue(Long id) {
		return Ebean.find(Reservation.class)
					.fetch("eventInstance")
					.fetch("eventInstance.venue")
					.where()
					.eq("id", id).findUnique();
	}

	private String generateQrCode(Reservation reservation) throws IOException, WriterException {
		DateVenuePk dateVenuePk = reservation.getEventInstance().getDateVenuePk();
		final QrCodeReservationInfo qrCodeReservationInfo = new QrCodeReservationInfo();
		qrCodeReservationInfo.setEventId(dateVenuePk.getEventId());
		qrCodeReservationInfo.setReservationId(reservation.getId());
		qrCodeReservationInfo.setVenueId(dateVenuePk.getVenueId());

		final String qrTicket = QrCodeHelper.getBase64QrCode(Json.toJson(qrCodeReservationInfo).toString());
		return qrTicket;
	}

	@NotNull
	private VisitorInfo getCommonVisitorInfo(Reservation r) {
		VisitorInfo visitorInfo;
		if (r.getGuest() != null) {
			visitorInfo = new VisitorInfo(r.getGuest());
		} else {
			visitorInfo = getVisitorInfoStub(r);
		}
		return visitorInfo;
	}

	@NotNull
	private VisitorInfo getVisitorInfoStub(Reservation r) {
		VisitorInfo visitorInfo;
		if (r.getGuestFullName() == null)
			Logger.warn("Reservation without user or username has been detected. id=" + r.getId());
		visitorInfo = new VisitorInfo();
		visitorInfo.setFullName(r.getGuestFullName());
		return visitorInfo;
	}

	@NotNull
	@Override
	public VisitorInfo getDetailedVisitorInfo(Long venueId, User visitor) {
		return getDetailedVisitorInfo(venueId, visitor, null, null);
	}

	@NotNull
	@Override
	public VisitorInfo getDetailedVisitorInfo(Long venueId, User visitor, List<Reservation> reservations, List<Feedback> feedbacks) {
		VisitorInfo visitorInfo = new VisitorInfo(visitor);

		final Long visitorId = visitor.getId();
		try {
			if (feedbacks == null) {
				feedbacks = getFeedbacksOfUserInVenue(venueId, visitorId);
			}

			Long avg = Math.round(feedbacks.stream()
					.mapToInt(Feedback::getStars)
					.average().orElse(0));

			if (reservations == null) {
				reservations = getAllReservationsOfUserInVenue(venueId, visitorId);
			}

			if (reservations != null) {
				reservations.forEach(reservation -> {
					if (reservation.getBottleService() != null) {
						visitorInfo.setBsVisits(visitorInfo.getBsReservations() + 1);
						if (reservation.getArrivalTime() != null)
							visitorInfo.setBsVisits(visitorInfo.getBsVisits() + 1);
					} else {
						visitorInfo.setGlVisits(visitorInfo.getGlReservations() + 1);
						if (reservation.getArrivalTime() != null)
							visitorInfo.setGlVisits(visitorInfo.getGlVisits() + 1);
					}
				});

				Integer totalSpent = reservations.stream()
						.filter(reservation -> reservation.getBottleService() != null)
						.mapToInt(Reservation::getTotalSpent).sum();

				final LocalDate[] lastBSReservationDate = {null};
				reservations.stream()
						.filter(reservation -> reservation.getBottleService() != null)
						.reduce((res, res2) -> {
							if (res.getEventInstance().getDate().isAfter(res2.getEventInstance().getDate()))
								return res;
							else
								return res2;
				}).ifPresent(reservation -> lastBSReservationDate[0] = reservation.getEventInstance().getDate());

				final LocalDate[] lastGLReservationDate = {null};
				reservations.stream()
						.filter(reservation -> reservation.getBottleService() == null)
						.reduce((res, res2) -> {
							if (res.getEventInstance().getDate().isAfter(res2.getEventInstance().getDate()))
								return res;
							else
								return res2;
				}).ifPresent(reservation -> lastGLReservationDate[0] = reservation.getEventInstance().getDate());

				visitorInfo.setTotalSpent(totalSpent);
				LocalDate lastBSDate = lastBSReservationDate[0];
				LocalDate lastGLDate = lastGLReservationDate[0];

				visitorInfo.setLastBSReservationDate(lastBSDate != null ? lastBSDate.toString() : "");
				visitorInfo.setLastGLReservationDate(lastGLDate != null ? lastGLDate.toString() : "");

				if (lastBSDate != null && lastGLDate != null) {
					final String lastDate = lastBSDate.isAfter(lastGLDate) ? lastBSDate.toString() : lastGLDate.toString();
					visitorInfo.setLastReservationDate(lastDate);
				} else if (lastBSDate != null) {
					visitorInfo.setLastReservationDate(lastBSDate.toString());
				} else if (lastGLDate != null) {
					visitorInfo.setLastReservationDate(lastGLDate.toString());
				}

				visitorInfo.setAvgSpent(getAvgSpent(reservations));
			}

			visitorInfo.setRating(avg.shortValue());
			visitorInfo.setFeedbackCount(feedbacks.size());
		} catch (Exception e) {
			Logger.warn("Can't count average rating of user feedback", e);
			throw e;
		}

		try {
			String sql = "SELECT" +
					" SUM(CASE WHEN bottle_service IS NOT NULL AND status != 'PENDING' THEN 1 ELSE 0 END) AS approved," +
					" SUM(CASE WHEN bottle_service IS NOT NULL AND status IN ('ARRIVED','PRE_RELEASED','RELEASED','COMPLETED','CONFIRMED_COMPLETE') THEN 1 ELSE 0 END) AS arrived" +
					" FROM reservation WHERE guest_id=:guest_id AND venue_id=:venue_id;";
			SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
			sqlQuery.setParameter("guest_id", visitorId);
			sqlQuery.setParameter("venue_id", venueId);
			final SqlRow sqlRow = sqlQuery.findUnique();
			final Integer arrived = sqlRow.getInteger("arrived");
			visitorInfo.setTotalVisits(arrived != null ? arrived : 0);
			final Integer approved = sqlRow.getInteger("approved");
			visitorInfo.setTotalReservations(approved != null ? approved : 0);
		} catch (Exception e) {
			Logger.warn("Can't count approved and arrived visits for guest_id=" + visitorId +
					"and venue_id=" + venueId, e);
			throw e;
		}
		return visitorInfo;
	}

	@Override
	public List<Feedback> getFeedbacksOfUserInVenue(Long venueId, Long visitorId) {
		final List<Feedback> feedbacks = Feedback.find.where()
				.eq("user_id", visitorId)
				.eq("reservation.eventInstance.dateVenuePk.venueId", venueId)
				.eq("meta", false)
				.findList();
		return feedbacks != null ? feedbacks : new ArrayList<>();
	}

	@Override
	public List<EODReservationInfo> getEodReservationInfos(List<Reservation> reservationList) {
		return reservationList.stream()
				.map(reservation -> reservationToEODInfo(reservation, true, true))
				.collect(Collectors.toList());
	}

	private int getAvgSpent(List<Reservation> pastReservations) {
		int avgSpent = 0;
		if (pastReservations != null) {
			final long totalSpent = pastReservations.stream().mapToLong(r -> r.getTotalSpent() != null ? r.getTotalSpent() : 0).sum();
			avgSpent = Math.round(totalSpent/pastReservations.size());
		}
		return avgSpent;
	}

	@Override
	public List<Reservation> getAllReservationsOfUserInVenue(Long venueId, Long guestId) {
		List<Reservation> reservations = Reservation.find
				.fetch("staff")
				.fetch("staff.user")
				.where()
				.and(
					Expr.eq("eventInstance.dateVenuePk.venueId", venueId),
					Expr.eq("guest.id", guestId)
				).findList();
		return reservations != null ? reservations : new ArrayList<>();
	}
}
