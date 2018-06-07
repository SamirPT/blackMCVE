package to;

/**
 * Created by arkady on 05/04/16.
 */
public class UserSettings {
	private Boolean postOnMyBehalf;
	private Boolean rsvpOnMyBehalf;
	private Boolean checkInOnMyBehalf;
	private Boolean smsMessages;
	private Boolean inAppNotification;

	public UserSettings() {
	}

	public UserSettings(Boolean postOnMyBehalf, Boolean rsvpOnMyBehalf, Boolean checkInOnMyBehalf, Boolean smsMessages, Boolean inAppNotification) {
		this.postOnMyBehalf = postOnMyBehalf;
		this.rsvpOnMyBehalf = rsvpOnMyBehalf;
		this.checkInOnMyBehalf = checkInOnMyBehalf;
		this.smsMessages = smsMessages;
		this.inAppNotification = inAppNotification;
	}

	public Boolean getPostOnMyBehalf() {
		return postOnMyBehalf;
	}

	public void setPostOnMyBehalf(Boolean postOnMyBehalf) {
		this.postOnMyBehalf = postOnMyBehalf;
	}

	public Boolean getRsvpOnMyBehalf() {
		return rsvpOnMyBehalf;
	}

	public void setRsvpOnMyBehalf(Boolean rsvpOnMyBehalf) {
		this.rsvpOnMyBehalf = rsvpOnMyBehalf;
	}

	public Boolean getCheckInOnMyBehalf() {
		return checkInOnMyBehalf;
	}

	public void setCheckInOnMyBehalf(Boolean checkInOnMyBehalf) {
		this.checkInOnMyBehalf = checkInOnMyBehalf;
	}

	public Boolean getSmsMessages() {
		return smsMessages;
	}

	public void setSmsMessages(Boolean smsMessages) {
		this.smsMessages = smsMessages;
	}

	public Boolean getInAppNotification() {
		return inAppNotification;
	}

	public void setInAppNotification(Boolean inAppNotification) {
		this.inAppNotification = inAppNotification;
	}
}
