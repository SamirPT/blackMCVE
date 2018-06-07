package to;

import java.util.List;

/**
 * Created by arkady on 05/04/16.
 */
public class Promotion {
	private String url;
	private String message;
	private List<Long> userIds;

	public Promotion() {
	}

	public Promotion(String url, String message, List<Long> userIds) {
		this.url = url;
		this.message = message;
		this.userIds = userIds;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<Long> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Long> userIds) {
		this.userIds = userIds;
	}
}
