package util;

import com.google.inject.ImplementedBy;
import models.BottleServiceType;
import models.Feedback;
import models.Reservation;
import models.User;
import to.ReservationInfo;
import to.VisitorInfo;
import to.guest.GuestReservationTicket;
import to.venue.EODReservationInfo;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by arkady on 13/07/16.
 */
@ImplementedBy(ReservationsHelperImpl.class)
public interface ReservationsHelper {
	ReservationInfo reservationToInfo(Reservation r, boolean detailed);
	ReservationInfo reservationToInfo(Reservation r, boolean detailed, boolean withStaff);
	List<Reservation> getReservationsWithBS(Long venueId, LocalDate date, BottleServiceType bottleServiceType, Long eventId);
	void notifyGuestBySMS(Reservation reservation);
	List<Reservation> getReservationsWithBSAndTables(Long venueId, LocalDate date, Long eventId);
	float getReservationRating(Long reservationId);
	GuestReservationTicket getGuestReservationTicket(String token);
	GuestReservationTicket getGuestReservationTicket(Long id);
	Reservation getReservationWithFetchedVenue(Long id);
	VisitorInfo getDetailedVisitorInfo(Long venueId, User visitor);
	void notifyOnArrival(Reservation reservation, String venueName);
	EODReservationInfo reservationToEODInfo(Reservation r, boolean detailed, boolean withStaff);
	boolean isReservationInApprovedList(Reservation r);
	List<Reservation> getAllReservationsOfUserInVenue(Long venueId, Long guestId);
	VisitorInfo getDetailedVisitorInfo(Long venueId, User visitor, List<Reservation> reservations, List<Feedback> feedbacks);
	List<Feedback> getFeedbacksOfUserInVenue(Long venueId, Long visitorId);
	List<EODReservationInfo> getEodReservationInfos(List<Reservation> reservationList);
}
