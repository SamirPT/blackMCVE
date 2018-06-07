package to.reports;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created by arkady on 12/06/16.
 */
public class ClickerReportPoint {
	@JsonIgnore
	private LocalDate localDate;
	@JsonIgnore
	private LocalTime localTime;
	private Long men;
	private Long woman;
	private String date;
	private String time;

	public ClickerReportPoint() {
	}

	public ClickerReportPoint(LocalDate localDate, LocalTime localTime, Long men, Long woman) {
		this.localDate = localDate;
		this.localTime = localTime;
		this.men = men;
		this.woman = woman;
	}

	public LocalDate getLocalDate() {
		return localDate;
	}

	public void setLocalDate(LocalDate localDate) {
		this.localDate = localDate;
	}

	public LocalTime getLocalTime() {
		return localTime;
	}

	public void setLocalTime(LocalTime localTime) {
		this.localTime = localTime;
	}

	public Long getMen() {
		return men;
	}

	public void setMen(Long men) {
		this.men = men;
	}

	public Long getWoman() {
		return woman;
	}

	public void setWoman(Long woman) {
		this.woman = woman;
	}

	public String getDate() {
		return localDate != null ? localDate.toString() : null;
	}

	public String getTime() {
		return this.localTime != null ? localTime.toString() : null;
	}
}
