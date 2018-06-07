package util;

import models.*;
import play.Logger;
import to.clicker.ClickerUpdate;
import util.exception.IllegalReservationDateException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Created by arkady on 07/05/16.
 */
public class ClickerHelper {

	public static EventInstance putUpdateEventInstance(EventInstance eventInstance, ClickerUpdate clickerUpdate) throws IllegalReservationDateException {
		if (clickerUpdate.getMen() > 0) {
			eventInstance.setTotalIn(eventInstance.getTotalIn() + clickerUpdate.getMen());
		} else {
			eventInstance.setTotalOut(eventInstance.getTotalOut() + Math.abs(clickerUpdate.getMen()));
		}

		if (clickerUpdate.getWomen() > 0) {
			eventInstance.setTotalIn(eventInstance.getTotalIn() + clickerUpdate.getWomen());
		} else {
			eventInstance.setTotalOut(eventInstance.getTotalOut() + Math.abs(clickerUpdate.getWomen()));
		}

		eventInstance.setMen(eventInstance.getMen() + clickerUpdate.getMen());
		eventInstance.setWomen(eventInstance.getWomen() + clickerUpdate.getWomen());

		try {
			eventInstance.update();
		} catch (Exception e) {
			Logger.warn("Can't update EventInstance from clicker", e);
			throw e;
		}

		return eventInstance;
	}

	public static LocalDate getCurrentWorkingDay(Venue venue) {
		final ZoneId zoneId = ZoneId.of(venue.getTimeZone());
		final LocalDate today = LocalDate.now(zoneId);
		List<Event> yesterdayEvents = EventsHelper.getEventListByDate(venue.getId(), today.minusDays(1L));

		final LocalTime[] lastYesterdaysEventEnd = {null};
		if (yesterdayEvents != null) {
			yesterdayEvents.stream()
					.filter(event -> !event.getStartsAt().isBefore(event.getEndsAt()))
					.max((o1, o2) -> o1.getEndsAt().compareTo(o2.getEndsAt()))
					.ifPresent(event -> lastYesterdaysEventEnd[0] = event.getEndsAt());
		}

		if (lastYesterdaysEventEnd[0] != null && LocalTime.now(zoneId).isBefore(lastYesterdaysEventEnd[0])) {
			return today.minusDays(1L);
		} else {
			return today;
		}
	}

	public static void updateClickerPoint(LocalTime localTime, LocalDate eventDate, Long venueId, Short men, Short woman) {
		LocalTime clickerPointTime;
		final int localTimeMinutes = localTime.getMinute();
		final int localTimeSeconds = localTime.getSecond();
		if (localTimeMinutes < 30) {
			clickerPointTime = localTime.plusMinutes(30 - localTimeMinutes).minusSeconds(localTimeSeconds);
		} else {
			clickerPointTime = localTime.plusMinutes(60 - localTimeMinutes).minusSeconds(localTimeSeconds);
		}

		ClickerPointPk clickerPointPk = new ClickerPointPk(venueId, eventDate, clickerPointTime);
		ClickerPoint clickerPoint = ClickerPoint.find.byId(clickerPointPk);
		if (clickerPoint == null) {
			clickerPoint = new ClickerPoint();
			clickerPoint.setClickerPointPk(clickerPointPk);

			try {
				clickerPoint.save();
			} catch (Exception e) {
				Logger.warn("Can't save new clicker point", e);
				throw e;
			}
		}
		clickerPoint.setMen(men);
		clickerPoint.setWomen(woman);

		try {
			clickerPoint.update();
		} catch (Exception e) {
			Logger.warn("Can't update clicker point", e);
			throw e;
		}
	}

	public static boolean isShowClicker(final LocalDate localDate, final Long eventId, final Long venueId, final ZoneId zoneId) {
		Event event = EventsHelper.isEventAtDate(venueId, localDate, eventId);
		if (event == null) {
			return false;
		}

		LocalDate dateOfEventEnd = event.getStartsAt().isBefore(event.getEndsAt()) ? localDate : localDate.plusDays(1L);
		LocalDateTime localDateTimeEventStart = LocalDateTime.of(localDate, event.getStartsAt());
		LocalDateTime localDateTimeEventEnd = LocalDateTime.of(dateOfEventEnd, event.getEndsAt());

		LocalDateTime localDateTime = LocalDateTime.now(zoneId);
		return !localDateTime.isBefore(localDateTimeEventStart.minusHours(1L)) &&
				!localDateTime.isAfter(localDateTimeEventEnd.plusHours(1L));
	}
}
