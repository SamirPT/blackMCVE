package to;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by arkady on 05/02/16.
 */
public class ApiKey {

	@JsonProperty("api_key")
	private String apiKey;

	public ApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
}
