package to.notifications;

/**
 * Created by arkady on 30/09/16.
 */
public enum NotificationType {
	BS_RESERVATION_MADE_WITH_NUMBER ("Your reservation at {venue} has been confirmed {res_info_url}"),
	NEW_APPROVAL_REQUEST_BS ("BS Res: {name}{minimums} - for {day_of_week}, {date}"),
	NEW_APPROVAL_EMPLOYEE ("New Employee Approval Request {name} {role}"),
	NOTIFY_ME_ARRIVAL ("{name}'s party has now arrived at {venue}, seated in {table}"),
	NEW_ASSIGNMENT ("You've been assigned a reservation {name}{guests} at {table}.{min}"),
	TABLE_RELEASED ("{employee_name} requested to release {table} ({client_name})"),
	RESERVATION_I_CREATED_APPROVED ("Reservation for {client_name} at {venue} has been approved"),
	GL_RESERVATION_MADE_WITH_NUMBER ("Your reservation at {venue} has been confirmed {res_info_url}"),
	RESERVATION_I_CREATED_REJECTED ("Your reservation at {venue} for {client_name} has been rejected");

	private String template;

	NotificationType(String template) {
		this.template = template;
	}

	public String getTemplate() {
		return template;
	}
}
