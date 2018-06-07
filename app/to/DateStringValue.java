package to;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by arkady on 20/03/16.
 */
public class DateStringValue {

	@ApiModelProperty(readOnly = true, example = "2016-10-02")
	private String date;

	public DateStringValue() {
	}

	public DateStringValue(String date) {
		this.date = date;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
