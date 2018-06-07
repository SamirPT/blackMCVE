package modules.push.to;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by arkady on 14/05/16.
 *
 * Example:
 * {
 * 		"data": {
 * 			"title": "Exodus Las Vegas",
 * 			"message": "New photos are uploaded! Check this out!!"
 * 		},
 * 		"to" : "cvgrg2UhQDY:APA91bHm5jb1fbfpL-V85WZW_0XtmagdF1cj4KxjXlq3XWcmXey26dR72Hd84rpuKDxUjc3b62p7lk_4T5ymZLwRMtswTcw0I1C39u0KrmLUc5IecqVSy6dN4P_wV59VmNFrh_RUPwVX"
 * }
 */
public class AndroidPushNotification {

	class Data {
		private String title;
		private String message;

		public Data() {
		}

		public Data(String title, String message) {
			this.title = title;
			this.message = message;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

	private Data data;
	@JsonProperty("registration_ids")
	private List<String> to;

	public AndroidPushNotification(String title, String message, List<String> to) {
		this.data = new Data(title, message);
		this.to = to;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}
}
