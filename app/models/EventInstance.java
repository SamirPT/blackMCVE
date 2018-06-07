package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by arkady on 11/03/16.
 */
@Entity
public class EventInstance extends Model {

	@EmbeddedId
	private DateVenuePk dateVenuePk = new DateVenuePk();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "venue_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
	private Venue venue;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false, insertable = false)
	private Event event;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Reservation> reservations;

	@Column(columnDefinition = "integer default 0")
	private Integer men = 0;
	@Column(columnDefinition = "integer default 0")
	private Integer women = 0;
	@Column(columnDefinition = "integer default 0")
	private Integer totalIn = 0;
	@Column(columnDefinition = "integer default 0")
	private Integer totalOut = 0;

	@Column(columnDefinition = "boolean default false")
	private Boolean bsClosed = false;
	@Column(columnDefinition = "boolean default false")
	private Boolean glClosed = false;
	@Column(columnDefinition = "boolean default false")
	private Boolean reportSent = false;
	private Integer minSpend;
	private Integer minBottles;

	public static Finder<DateVenuePk, EventInstance> finder = new Finder<>(EventInstance.class);

	public EventInstance() {
	}

	public EventInstance(DateVenuePk dateVenuePk) {
		this.dateVenuePk = dateVenuePk;
	}

	public DateVenuePk getDateVenuePk() {
		return dateVenuePk;
	}

	public void setDateVenuePk(DateVenuePk dateVenuePk) {
		this.dateVenuePk = dateVenuePk;
	}

	public LocalDate getDate() {
		return dateVenuePk.getDate();
	}

	public void setDate(LocalDate date) {
		dateVenuePk.setDate(date);
	}

	public Venue getVenue() {
		return venue;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	public List<Reservation> getReservations() {
		return reservations;
	}

	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}

	public Integer getMen() {
		return men;
	}

	public void setMen(Integer men) {
		this.men = men;
	}

	public Integer getWomen() {
		return women;
	}

	public void setWomen(Integer women) {
		this.women = women;
	}

	public Integer getTotalIn() {
		return totalIn;
	}

	public void setTotalIn(Integer totalIn) {
		this.totalIn = totalIn;
	}

	public Integer getTotalOut() {
		return totalOut;
	}

	public void setTotalOut(Integer totalOut) {
		this.totalOut = totalOut;
	}

	public Boolean getBsClosed() {
		return bsClosed;
	}

	public void setBsClosed(Boolean bsClosed) {
		this.bsClosed = bsClosed;
	}

	public Boolean getGlClosed() {
		return glClosed;
	}

	public void setGlClosed(Boolean glClosed) {
		this.glClosed = glClosed;
	}

	public Integer getMinSpend() {
		return minSpend;
	}

	public void setMinSpend(Integer minSpend) {
		this.minSpend = minSpend;
	}

	public Integer getMinBottles() {
		return minBottles;
	}

	public void setMinBottles(Integer minBottles) {
		this.minBottles = minBottles;
	}

	@Override
	public String toString() {
		return "EventInstance{" +
				"dateVenuePk=" + dateVenuePk +
				", venue=" + venue +
				", event=" + event +
				", reservations=" + reservations +
				", men=" + men +
				", women=" + women +
				", totalIn=" + totalIn +
				", totalOut=" + totalOut +
				", bsClosed=" + bsClosed +
				", glClosed=" + glClosed +
				", minSpend=" + minSpend +
				", minBottles=" + minBottles +
				'}';
	}

	public boolean getReportSent() {
		return reportSent;
	}

	public void setReportSent(Boolean reportSent) {
		this.reportSent = reportSent;
	}
}
