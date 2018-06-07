package to;

/**
 * Created by arkady on 16/02/16.
 */
public class ResponseMessage {
	String response;

	public ResponseMessage() {
		this.response = "Ok";
	}

	public ResponseMessage(String response) {
		this.response = response;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
}
