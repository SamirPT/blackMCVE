package util;

import com.google.inject.ImplementedBy;
import models.Notification;
import models.Reservation;
import models.UserVenue;

import java.time.LocalDate;
import java.util.Map;

/**
 * Created by arkady on 30/09/16.
 */
@ImplementedBy(NotificationsHelperImpl.class)
public interface NotificationsHelper {
	void sendNewApprovalRequestBSNotification(Reservation reservation, Notification notification, String iosToken);
	Map<String, String> getNewApprovalRequestBSNotificationData(Reservation reservation, LocalDate localDate);

	void sendNewApprovalEmployeeNotification(Notification notification, String iosToken);
	Map<String, String> getNewApprovalEmployeeNotificationData(UserVenue userVenue);

	void sendNotifyMeArrivalNotification(Notification notification, String iosToken);
	Map<String, String> getNotifyMeArrivalNotificationData(Reservation reservation);

	void sendNewAssignmentNotification(Notification notification, String iosToken);
	Map<String, String> getNewAssignmentNotificationData(Reservation reservation);

	void sendTableReleasedNotification(Notification notification, String iosToken);
	Map<String, String> getTableReleasedNotificationData(Reservation reservation, String employeeName);

	void sendReservationICreatedApprovedNotification(Notification notification, String iosToken);
	Map<String, String> getReservationICreatedApprovedNotificationData(Reservation reservation);

	void sendReservationICreatedRejectedNotification(Notification notification, String iosToken);
	Map<String, String> getReservationICreatedRejectedNotificationData(Reservation reservation);
}
