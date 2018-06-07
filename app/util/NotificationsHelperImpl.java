package util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import models.*;
import modules.push.Push;
import org.apache.commons.lang3.StringUtils;
import play.libs.Json;
import to.notifications.NotificationTO;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by arkady on 30/09/16.
 */
@Singleton
public class NotificationsHelperImpl implements NotificationsHelper {

	private Push push;

	@Inject
	public NotificationsHelperImpl(Push push) {
		this.push = push;
	}

	/**
	 * BS Res: {name}{minimums} - for {day_of_week}, {date}
	 */
	public void sendNewApprovalRequestBSNotification(Reservation reservation, Notification notification, String iosToken) {
		NotificationTO notificationTO = new NotificationTO(notification);
		sendPush(iosToken, notificationTO);
	}

	public Map<String, String> getNewApprovalRequestBSNotificationData(Reservation reservation, LocalDate localDate) {
		Map<String, String> data = new HashMap<>();
		data.put("name", getGuestName(reservation));

		String minimums = "";
		if (reservation.getMinSpend() != null && reservation.getMinSpend() > 0) {
			minimums = " min $" + reservation.getMinSpend();
		} else if (reservation.getBottleMin() != null && reservation.getBottleMin() > 0) {
			minimums = " " + reservation.getBottleMin() + " bottle(s) min";
		}

		data.put("minimums", minimums);
		data.put("day_of_week", localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
		data.put("date", localDate.toString());
		return data;
	}

	/**
	 * New Employee Approval Request {name} {role}
	 */
	public void sendNewApprovalEmployeeNotification(Notification notification, String iosToken) {
		NotificationTO notificationTO = new NotificationTO(notification);
		sendPush(iosToken, notificationTO);
	}

	public Map<String, String> getNewApprovalEmployeeNotificationData(UserVenue userVenue) {
		Map<String, String> data = new HashMap<>();
		data.put("name", userVenue.getUser().getFullName());

		final String roles = userVenue.getRoles().stream()
				.map(VenueRole::toString)
				.collect(Collectors.joining(", "));
		data.put("role", roles);
		return data;
	}

	/**
	 * {name}'s party has now arrived at {venue}, seated in {table}
	 */
	public void sendNotifyMeArrivalNotification(Notification notification, String iosToken) {
		NotificationTO notificationTO = new NotificationTO(notification);
		sendPush(iosToken, notificationTO);
	}

	public Map<String, String> getNotifyMeArrivalNotificationData(Reservation reservation) {
		Map<String, String> data = new HashMap<>();
		data.put("name", getGuestName(reservation));
		data.put("venue", reservation.getEventInstance().getVenue().getName());
		data.put("table", getTableName(reservation));
		return data;
	}

	/**
	 * You've been assigned a reservation {name}{guests} at {table}. {min}
	 */
	public void sendNewAssignmentNotification(Notification notification, String iosToken) {
		NotificationTO notificationTO = new NotificationTO(notification);
		sendPush(iosToken, notificationTO);
	}

	public Map<String, String> getNewAssignmentNotificationData(Reservation reservation) {
		Map<String, String> data = new HashMap<>();

		data.put("name", getGuestName(reservation));

		String guestsNumber = "";
		if (reservation.getGuestsNumber() > 0) {
			guestsNumber = " + " + reservation.getGuestsNumber();
		}
		data.put("guests", guestsNumber);

		data.put("table", getTableName(reservation));

		String minimums = "";
		if (reservation.getMinSpend() != null && reservation.getMinSpend() > 0) {
			minimums = "Minimum spend: $" + reservation.getMinSpend();
		} else if (reservation.getBottleMin() != null && reservation.getBottleMin() > 0) {
			minimums = reservation.getBottleMin() + " bottle(s) min";
		}

		data.put("min", minimums);
		return data;
	}

	/**
	 * {employee_name} requested to release {table} ({client_name})
	 */
	public void sendTableReleasedNotification(Notification notification, String iosToken) {
		NotificationTO notificationTO = new NotificationTO(notification);
		sendPush(iosToken, notificationTO);
	}

	public Map<String, String> getTableReleasedNotificationData(Reservation reservation, String employeeName) {
		Map<String, String> data = new HashMap<>();
		data.put("employee_name", employeeName);
		data.put("table", getTableName(reservation));
		data.put("client_name", getGuestName(reservation));
		return data;
	}

	/**
	 * Reservation for {client_name} at {venue} has been approved
	 */
	public void sendReservationICreatedApprovedNotification(Notification notification, String iosToken) {
		NotificationTO notificationTO = new NotificationTO(notification);
		sendPush(iosToken, notificationTO);
	}

	public Map<String, String> getReservationICreatedApprovedNotificationData(Reservation reservation) {
		Map<String, String> data = new HashMap<>();
		data.put("client_name", getGuestName(reservation));
		data.put("venue", reservation.getEventInstance().getVenue().getName());
		return data;
	}

	/**
	 * Your reservation at {venue} for {client_name} has been rejected
	 */
	public void sendReservationICreatedRejectedNotification(Notification notification, String iosToken) {
		NotificationTO notificationTO = new NotificationTO(notification);
		sendPush(iosToken, notificationTO);
	}

	public Map<String, String> getReservationICreatedRejectedNotificationData(Reservation reservation) {
		Map<String, String> data = new HashMap<>();
		data.put("client_name", getGuestName(reservation));
		data.put("venue", reservation.getEventInstance().getVenue().getName());
		return data;
	}

	private String getNotificationTitle(NotificationTO notificationTO) {
		final String[] rawMessage = {notificationTO.getMessage()};
		if (StringUtils.isNotEmpty(rawMessage[0])) {
			notificationTO.getData().forEach((s, s2) -> rawMessage[0] = rawMessage[0].replace("{" + s + "}", s2));
		}
		return rawMessage[0];
	}

	private void sendPush(String iosToken, NotificationTO notificationTO) {
		final String title = getNotificationTitle(notificationTO);
		push.sendIOSPush(iosToken, title, Json.toJson(notificationTO).toString());
	}

	private String getGuestName(Reservation reservation) {
		return reservation.getGuest() != null ? reservation.getGuest().getFullName() : reservation.getGuestFullName();
	}

	private String getTableName(Reservation reservation) {
		Place table = reservation.getTable() != null ? reservation.getTable() : reservation.getReleasedFrom();
		if (table == null) return "";
		return table.getBottleServiceType().getName() + " #" + table.getPlaceNumber();
	}
}
