package util;

import com.avaje.ebean.Model;
import models.DateVenuePk;
import models.Event;
import models.EventInstance;
import models.Venue;
import util.exception.IllegalReservationDateException;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by arkady on 19/03/16.
 */
public class EventInstanceHelper {

	public static EventInstance getOrCreateEventInstance(Long venueId, LocalDate reservationDate, Long eventId) throws IllegalReservationDateException {
		return getOrCreateEventInstance(venueId, reservationDate, eventId, false, false, false);
	}

	public static EventInstance getOrCreateEventInstance(Long venueId, LocalDate reservationDate, Long eventId, boolean fetchVenue, boolean fetchEvent, boolean fetchReservations) throws IllegalReservationDateException {
		List<Event> events = EventsHelper.getEventListByDate(venueId, reservationDate);
		if (events == null || events.size() == 0) {
			throw new IllegalReservationDateException("No events at this date for given Venue");
		}

		Event event = null;
		for (Event e : events) {
			if (e.getId().equals(eventId)) {
				event = e;
				break;
			}
		}

		if (event == null) {
			throw new IllegalReservationDateException("Bad Event id or this Event is not today");
		}

		final Model.Finder<DateVenuePk, EventInstance> finder = EventInstance.finder;

		if (fetchVenue) finder.fetch("venue");
		if (fetchEvent) finder.fetch("event");
		if (fetchReservations) finder.fetch("reservations");

		EventInstance eventInstance = finder
				.where()
				.eq("venue.id", venueId)
				.eq("event.id", eventId)
				.eq("dateVenuePk.date", reservationDate)
				.findUnique();

		if (eventInstance == null) {
			if (event.isDeleted()) {
				throw new IllegalReservationDateException("This Event has been deleted");
			}

			Venue venue = Venue.finder.byId(venueId);
			if (venue == null) {
				throw new IllegalReservationDateException("Unknown Venue id");
			}

			DateVenuePk dateVenuePk = new DateVenuePk(venueId, reservationDate, eventId);
			eventInstance = new EventInstance(dateVenuePk);
			eventInstance.setVenue(venue);
			eventInstance.setEvent(event);
			eventInstance.save();
		}
		return eventInstance;
	}
}
