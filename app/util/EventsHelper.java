package util;

import com.avaje.ebean.*;
import controllers.api.v1.Events;
import models.Event;
import play.Logger;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by arkady on 19/03/16.
 */
public class EventsHelper {

	public static List<Event> getEventListByDate(Long venueId, LocalDate date) {
		List<Event> eventList;
		try {
			eventList = Event.find
					.where()
					.eq("venueId", venueId)
					.or(
							Expr.and(
									Expr.raw("(('" + date + "' - date) % 7 = 0 AND repeatable = true AND '" + date + "' > date)"),
									Expr.or(
											Expr.eq("deleted", false),
											Expr.gt("deleted_at", date)
									)
							),
							Expr.and(
									Expr.eq("date", date),
									Expr.or(
											Expr.eq("deleted", false),
											Expr.gt("deleted_at", date)
									)
							)
					)
					.findList();
		} catch (Exception e) {
			Logger.warn("Can't get event list by date", e);
			throw e;
		}
		return eventList;
	}

	public static Event isEventAtDate(Long venueId, LocalDate date, Long eventId) {
		try {
			Event event = Event.find.where().eq("venueId", venueId).eq("id", eventId).or(
					Expr.raw("(('" + date + "' - date) % 7 = 0 AND repeatable = true AND '" + date + "' > date)" ),
					Expr.eq("date", date)
			).findUnique();
			return event;
		} catch (Exception e) {
			Logger.warn("Can't get event by date", e);
			throw e;
		}
	}

	public static LocalDate getClosestEventAfterDate(Long venueId, LocalDate fromDate) {
		String sqlRepeatable = "SELECT date, (:date - date) % 7 AS ddiff" +
				" FROM event" +
				" WHERE venue_id = :venueId" +
				"  AND repeatable = true" +
				"  AND :date > date " +
				"  AND (deleted = false OR deleted_at > :date) " +
				"ORDER BY ddiff DESC LIMIT 1;";
		SqlQuery sqlQuery = Ebean.createSqlQuery(sqlRepeatable);
		sqlQuery.setParameter("date", fromDate);
		sqlQuery.setParameter("venueId", venueId);
		final SqlRow repeatable = sqlQuery.findUnique();

		LocalDate closestRepeatable = null;
		if (repeatable != null) {
			final Long ddiff = repeatable.getLong("ddiff");
			closestRepeatable = fromDate.plusDays(7 - ddiff);
		}

		String sqlSingle = "SELECT date, date - :date as ddiff FROM event WHERE venue_id = :venueId " +
				"AND :date < date " +
				"AND (deleted = false OR deleted_at > :date) " +
				"ORDER BY ddiff ASC LIMIT 1;";
		sqlQuery = Ebean.createSqlQuery(sqlSingle);
		sqlQuery.setParameter("date", fromDate);
		sqlQuery.setParameter("venueId", venueId);
		final SqlRow single = sqlQuery.findUnique();

		LocalDate closestSingle = null;
		if (single != null) {
			closestSingle = LocalDate.parse(single.getString("date"));
		}

		if (closestRepeatable != null && closestSingle != null) {
			if (closestRepeatable.isBefore(closestSingle)) {
				return closestRepeatable;
			} else {
				return closestSingle;
			}
		} else if (closestRepeatable != null) {
			return closestRepeatable;
		} else {
			return closestSingle;
		}
	}

	public static LocalDate getClosestEventBeforeDate(Long venueId, LocalDate fromDate) {
		String sqlRepeatable = "SELECT * FROM" +
				" (SELECT date, " +
				"  CASE ((:date - date) % 7)" +
				"  WHEN 0 THEN 7" +
				"  ELSE (:date - date) % 7" +
				"  END ddiff" +
				" FROM event" +
				" WHERE venue_id = :venueId" +
				"  AND repeatable = true" +
				"  AND (deleted = false OR deleted_at > :date)" +
				"  AND :date > date) as evt " +
				"ORDER BY ddiff ASC LIMIT 1;";
		SqlQuery sqlQuery = Ebean.createSqlQuery(sqlRepeatable);
		sqlQuery.setParameter("date", fromDate);
		sqlQuery.setParameter("venueId", venueId);
		final SqlRow repeatable = sqlQuery.findUnique();

		LocalDate closestRepeatable = null;
		if (repeatable != null) {
			final Long ddiff = repeatable.getLong("ddiff");
			closestRepeatable = fromDate.minusDays(ddiff);
		}

		String sqlSingle = "SELECT date FROM event WHERE venue_id = :venueId " +
				"AND (repeatable = false OR repeatable IS NULL) " +
				"AND (deleted = false OR deleted_at > :date) " +
				"AND :date > date ORDER BY date DESC LIMIT 1;";
		sqlQuery = Ebean.createSqlQuery(sqlSingle);
		sqlQuery.setParameter("date", fromDate);
		sqlQuery.setParameter("venueId", venueId);
		final SqlRow single = sqlQuery.findUnique();

		LocalDate closestSingle = null;
		if (single != null) {
			closestSingle = LocalDate.parse(single.getString("date"));
		}

		if (closestRepeatable != null && closestSingle != null) {
			if (closestRepeatable.isAfter(closestSingle)) {
				return closestRepeatable;
			} else {
				return closestSingle;
			}
		} else if (closestRepeatable != null) {
			return closestRepeatable;
		} else {
			return closestSingle;
		}
	}

	public static List<Event> getEventsForMonth(LocalDate startDate, LocalDate endDate, Long venueId) {
		List<Event> eventsList = Event.find
				.where()
				.eq("venueId", venueId)
				.or(
					//find all recurring events, which starts in this months or early
					Expr.and(
							Expr.le("date", endDate),
							Expr.eq("repeatable", true)
					),
					//find all NON-recurring events, which starts in this months
					Expr.and(
							Expr.and(
									Expr.ge("date", startDate),
									Expr.le("date", endDate)
							),
							Expr.or(
									Expr.eq("repeatable", false),
									Expr.isNull("repeatable")
							)
					)
				).findList();

		return eventsList;
	}
}
