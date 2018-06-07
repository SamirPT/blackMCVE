package models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by arkady on 30/09/16.
 */
public class NotificationData {

	private Map<String,String> data = new HashMap<>();

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}
}
