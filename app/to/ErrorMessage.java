package to;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import play.libs.Json;

/**
 * Created by arkady on 05/02/16.
 */
public class ErrorMessage {
	private String error;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String fields;

	public ErrorMessage(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public void addField(String field) {
		if (StringUtils.isEmpty(this.fields)) {
			this.fields = field;
		} else {
			this.fields = this.fields + "," + field;
		}
	}

	public static JsonNode getJsonInternalServerErrorMessage() {
		return getJsonErrorMessage("Something went wrong. Please contact application team");
	}

	public static JsonNode getJsonForbiddenMessage() {
		return getJsonErrorMessage("Your role in this venue doesn't permit this action, consult your manager");
	}

	public static JsonNode getJsonErrorMessage(String message) {
		return Json.toJson(new ErrorMessage(message));
	}
}
