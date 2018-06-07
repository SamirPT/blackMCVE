package tech.peller.mrblackandroidwatch.models.user;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class ApiKey extends RealmObject {
	@SerializedName("api_key")
	private String apiKey;

	public ApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public ApiKey() {
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
}
