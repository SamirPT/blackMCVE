package controllers.api.v1;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;
import com.google.inject.Inject;
import io.swagger.annotations.*;
import models.ClickerPoint;
import models.Reservation;
import models.ReservationStatus;
import models.User;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import to.ErrorMessage;
import to.ResponseMessage;
import to.SearchUserInfo;
import to.reports.*;
import util.ReservationsHelper;
import util.exception.IllegalInputParameterStateException;
import util.exception.IllegalReportTypeException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by arkady on 31/03/16.
 */
@Api(value = "/api/v1/reports", description = "Reports", tags = "reports", basePath = "/api/v1/reports")
public class Reports extends UserProfileController {

	private ReservationsHelper reservationsHelper;

	@Inject
	public Reports(ReservationsHelper reservationsHelper) {
		this.reservationsHelper = reservationsHelper;
	}

	@ApiOperation(
			nickname = "getPdfReservationsReport",
			value = "Sent reservations Report PDF",
			notes = "Sent reservations Report PDF to user by api_key",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 400, message = "dateTo, venueId and reportType are required params | " +
							"Unknown reportType param | Bad values of dateFrom or dateTo params | " +
							"dateTo param required for ReportType.INTERVAL", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getReservationsPdfReport(@ApiParam(required = true) String dateTo, @ApiParam(value = "Only for reportType.INTERVAL") String dateFrom,
										   @ApiParam(required = true) Long venueId,
										   @ApiParam(required = true, allowableValues = "DAILY, WEEKLY, MONTHLY, INTERVAL") String reportType) {
		return ok(Json.toJson(new ResponseMessage("Ok")));
	}

	@ApiOperation(
			nickname = "getPdfPromotersReport",
			value = "Sent promoters Report PDF",
			notes = "Sent promoters Report PDF to user by api_key",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 400, message = "dateTo, venueId and reportType are required params | " +
							"Unknown reportType param | Bad values of dateFrom or dateTo params | " +
							"dateTo param required for ReportType.INTERVAL", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getPromotersPdfReport(@ApiParam(required = true) String dateTo, @ApiParam(value = "Only for reportType.INTERVAL") String dateFrom,
										@ApiParam(required = true) Long venueId,
										@ApiParam(required = true, allowableValues = "DAILY, WEEKLY, MONTHLY, INTERVAL") String reportType) {
		return ok(Json.toJson(new ResponseMessage("Ok")));
	}

	@ApiOperation(
			nickname = "getPdfReservationsReport",
			value = "Sent employees Report PDF",
			notes = "Sent employees Report PDF to user by api_key",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 400, message = "dateTo, venueId and reportType are required params | " +
							"Unknown reportType param | Bad values of dateFrom or dateTo params | " +
							"dateTo param required for ReportType.INTERVAL", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getEmployeesPdfReport(@ApiParam(required = true) String dateTo, @ApiParam(value = "Only for reportType.INTERVAL") String dateFrom,
										@ApiParam(required = true) Long venueId,
										@ApiParam(required = true, allowableValues = "DAILY, WEEKLY, MONTHLY, INTERVAL") String reportType) {
		return ok(Json.toJson(new ResponseMessage("Ok")));
	}

	@ApiOperation(
			nickname = "getPdfReservationsReport",
			value = "Sent clicker Report PDF",
			notes = "Sent clicker Report PDF to user by api_key",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 400, message = "dateTo, venueId and reportType are required params | " +
							"Unknown reportType param | Bad values of dateFrom or dateTo params | " +
							"dateTo param required for ReportType.INTERVAL", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getClickerPdfReport(@ApiParam(required = true) String dateTo, @ApiParam(value = "Only for reportType.INTERVAL") String dateFrom,
									  @ApiParam(required = true) Long venueId,
									  @ApiParam(required = true, allowableValues = "DAILY, WEEKLY, MONTHLY, INTERVAL") String reportType) {
		return ok(Json.toJson(new ResponseMessage("Ok")));
	}

	@ApiOperation(
			nickname = "getReservationsReport",
			value = "Get reservations Report",
			notes = "Get reservations Report",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.reports.Report.class),
					@ApiResponse(code = 400, message = "dateTo, venueId and reportType are required params | " +
							"Unknown reportType param | Bad values of dateTo or dateTo params | " +
							"dateTo param required for ReportType.INTERVAL", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getReservationsReport(@ApiParam(required = true) String dateTo, @ApiParam(value = "Only for reportType.INTERVAL") String dateFrom,
										@ApiParam(required = true) Long venueId,
										@ApiParam(required = true, allowableValues = "DAILY, WEEKLY, MONTHLY, INTERVAL") String reportType) {
		InputParameters inputParameters;
		try {
			inputParameters = new InputParameters(dateFrom, dateTo, venueId, reportType).invoke();
		} catch (IllegalInputParameterStateException e) {
			return badRequest(Json.toJson(new ErrorMessage(e.getMessage())));
		}
		LocalDate startDate = inputParameters.getStartDateValue();
		LocalDate endDate = inputParameters.getEndDateValue();

		List<Reservation> reservations;
		try {
			reservations = getReservationList(venueId, endDate, startDate);
		} catch (IllegalReportTypeException e) {
			Logger.warn("Bad reportType param: " + reportType);
			return badRequest(Json.toJson(new ErrorMessage("Unknown reportType param")));
		} catch (Exception e) {
			Logger.warn("Can't get reservation list for preparing report", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		if (reservations == null) {
			return notFound(Json.toJson(new ErrorMessage("No reservations for this period")));
		}

		List<ReservationItem> reservationItems = new ArrayList<>();
		for (Reservation r : reservations) {
			ReservationItem reservationItem = getReservationItem(r);
			fillCommonReservationItemParams(r, reservationItem);
			reservationItems.add(reservationItem);
		}

		Report report = new Report(reservationItems);
		return ok(Json.toJson(report));
	}

	private ReservationItem getReservationItem(Reservation r) {
		ReservationItem reservationItem = new ReservationItem();
		reservationItem.setDate(r.getEventInstance().getDate().toString());
		if (r.getGuest() != null) {
			reservationItem.setFullName(r.getGuest().getFullName());
			reservationItem.setUserpic(r.getGuest().getUserpic());
		} else {
			reservationItem.setFullName(r.getGuestFullName());
		}
		reservationItem.setReservedBy(r.getBookedBy().getFullName());
		reservationItem.setRating((short)Math.round(reservationsHelper.getReservationRating(r.getId())));
		if (r.getArrivalTime() != null) reservationItem.setArrived(r.getArrivalTime().toString());
		return reservationItem;
	}

	private List<Reservation> getReservationList(Long venueId, LocalDate endDate, LocalDate startDate) throws IllegalReportTypeException {
		final Model.Finder<Long, Reservation> finder = Reservation.find;
		ExpressionList<Reservation> reservationsQuery = finder.where().eq("eventInstance.dateVenuePk.venueId", venueId);
		if (endDate.equals(startDate)) {
			reservationsQuery.eq("eventInstance.dateVenuePk.date", endDate);
		} else {
			reservationsQuery.and(
				Expr.ge("eventInstance.dateVenuePk.date", startDate),
				Expr.le("eventInstance.dateVenuePk.date", endDate)
			);
		}
		return reservationsQuery.findList();
	}

	@ApiOperation(
			nickname = "getPromotersReport",
			value = "Get promoters Report",
			notes = "Get promoters Report",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.reports.PromotersReport.class),
					@ApiResponse(code = 400, message = "dateTo, venueId and reportType are required params | " +
							"Unknown reportType param | Bad values of dateFrom or dateTo params | " +
							"dateTo param required for ReportType.INTERVAL", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getPromotersReport(@ApiParam(required = true) String dateTo, @ApiParam(value = "Only for reportType.INTERVAL") String dateFrom,
									 @ApiParam(required = true) Long venueId,
									 @ApiParam(required = true, allowableValues = "DAILY, WEEKLY, MONTHLY, INTERVAL") String reportType) {
		InputParameters inputParameters;
		try {
			inputParameters = new InputParameters(dateFrom, dateTo, venueId, reportType).invoke();
		} catch (IllegalInputParameterStateException e) {
			return badRequest(Json.toJson(new ErrorMessage(e.getMessage())));
		}
		LocalDate startDate = inputParameters.getStartDateValue();
		LocalDate endDate = inputParameters.getEndDateValue();

		List<Reservation> reservations;
		try {
			reservations = getReservationList(venueId, endDate, startDate);
		} catch (IllegalReportTypeException e) {
			Logger.warn("Bad reportType param: " + reportType);
			return badRequest(Json.toJson(new ErrorMessage("Unknown reportType param")));
		} catch (Exception e) {
			Logger.warn("Can't get reservation list for preparing report", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		if (reservations == null) {
			return notFound(Json.toJson(new ErrorMessage("No reservations for this period")));
		}

		List<PromotedReservationItem> reservationItems = new ArrayList<>();
		Map<Long, Promoter> promotersMap = new HashMap<>();
		for (Reservation r : reservations) {
			User bookedBy = User.finder.fetch("promotionCompany").where().eq("id", r.getBookedBy().getId()).findUnique();
			if (bookedBy == null || bookedBy.getPromotionCompany() == null) continue;

			final Promoter promoter = new Promoter(bookedBy.getPromotionCompany());
			promotersMap.put(bookedBy.getPromotionCompany().getId(), promoter);

			PromotedReservationItem reservationItem = new PromotedReservationItem(getReservationItem(r));
			fillCommonReservationItemParams(r, reservationItem);
			reservationItem.setPromotionCompany(promoter);
			reservationItems.add(reservationItem);
		}

		PromotersReport promotersReport = new PromotersReport(new ArrayList<>(promotersMap.values()), reservationItems);
		return ok(Json.toJson(promotersReport));
	}

	@ApiOperation(
			nickname = "getEmployeesReport",
			value = "Get employees Report",
			notes = "Get employees Report",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.reports.EmployeesReport.class),
					@ApiResponse(code = 400, message = "dateTo, venueId and reportType are required params | " +
							"Unknown reportType param | Bad values of dateFrom or dateTo params | " +
							"dateTo param required for ReportType.INTERVAL", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getEmployeesReport(@ApiParam(required = true) String dateTo, @ApiParam(value = "Only for reportType.INTERVAL") String dateFrom,
									 @ApiParam(required = true) Long venueId,
									 @ApiParam(required = true, allowableValues = "DAILY, WEEKLY, MONTHLY, INTERVAL") String reportType) {
		InputParameters inputParameters;
		try {
			inputParameters = new InputParameters(dateFrom, dateTo, venueId, reportType).invoke();
		} catch (IllegalInputParameterStateException e) {
			return badRequest(Json.toJson(new ErrorMessage(e.getMessage())));
		}
		LocalDate startDate = inputParameters.getStartDateValue();
		LocalDate endDate = inputParameters.getEndDateValue();

		List<Reservation> reservations;
		try {
			reservations = getReservationList(venueId, endDate, startDate);
		} catch (IllegalReportTypeException e) {
			Logger.warn("Bad reportType param: " + reportType);
			return badRequest(Json.toJson(new ErrorMessage("Unknown reportType param")));
		} catch (Exception e) {
			Logger.warn("Can't get reservation list for preparing report", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		if (reservations == null) {
			return notFound(Json.toJson(new ErrorMessage("No reservations for this period")));
		}

		List<ReservationItem> reservationItems = new ArrayList<>();
		Map<Long, SearchUserInfo> emoloyeesMap = new HashMap<>();
		for (Reservation r : reservations) {
			final User bookedBy = r.getBookedBy();
			final Long bookedById = bookedBy.getId();
			emoloyeesMap.put(bookedById, new SearchUserInfo(bookedById, bookedBy.getFullName(), bookedBy.getPhoneNumber()));

			ReservationItem reservationItem = getReservationItem(r);
			fillCommonReservationItemParams(r, reservationItem);
			reservationItem.setReservedById(bookedById);
			reservationItems.add(reservationItem);
		}

		EmployeesReport employeesReport = new EmployeesReport(new ArrayList<>(emoloyeesMap.values()), reservationItems);
		return ok(Json.toJson(employeesReport));
	}

	private void fillCommonReservationItemParams(Reservation r, ReservationItem reservationItem) {
		reservationItem.setBottleServiceType(r.getBottleService());
		reservationItem.setGuestsBooked(r.getGuestsNumber() + 1); //+1 for owner of reservation
		reservationItem.setGuestsActual((short) (r.getGuysSeated() + r.getGirlsSeated()));
		reservationItem.setIsActualReservation(ReservationStatus.ARRIVED.equals(r.getStatus()));
		reservationItem.setMinSpend(r.getMinSpend());
		reservationItem.setActualSpent(r.getTotalSpent());
	}

	/**
	 * Returns maximum 12 points of data. This is the maximum amount of points for small mobile screens.
	 * So if the difference between dateToParam and dateFromParam will be less than 6 hours, data will be
	 * represented as 30 minutes cuts. If the difference will be more than 6 hours, but less than 12 hours, data will be
	 * represented as hour cuts. Etc:
	 * > 12 hours and < day: 2 hours cuts,
	 * > day: day cuts,
	 * > 12 days: week cuts,
	 * > 12 weeks: month cuts,
	 * > 12 month: year cuts
	 */
	@ApiOperation(
			nickname = "getClickerReport",
			value = "Get clicker Report",
			notes = "Get clicker Report",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Clicker report points", response = ClickerReportPoint[].class),
					@ApiResponse(code = 400, message = "dateTo, venueId and reportType are required params | " +
							"Unknown reportType param | Bad values of dateFrom or dateTo params | " +
							"dateTo param required for ReportType.INTERVAL", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getClickerReport(@ApiParam(required = true) String dateToParam,
								   @ApiParam(value = "Only for reportType.INTERVAL") String dateFromParam,
								   @ApiParam(required = true) Long venueId,
								   @ApiParam(required = true) String reportType) {
		InputParameters inputParameters;
		try {
			inputParameters = new InputParameters(dateFromParam, dateToParam, venueId, reportType).invoke();
		} catch (IllegalInputParameterStateException e) {
			return badRequest(Json.toJson(new ErrorMessage(e.getMessage())));
		}
		LocalDate startDate = inputParameters.getStartDateValue();
		LocalDate endDate = inputParameters.getEndDateValue();

		List<ClickerReportPoint> points = new ArrayList<>();
		if (startDate.equals(endDate)) {
			List<ClickerPoint> clickerPoints = ClickerPoint.find.where()
					.eq("venue_id", venueId)
					.eq("date", endDate).findList();

			if (clickerPoints != null && clickerPoints.size() > 0) {
				LocalTime timeMin = null;
				LocalTime timeMax = null;
				for (ClickerPoint point : clickerPoints) {
					final LocalTime time = point.getClickerPointPk().getTime();
					if (timeMin == null || time.isBefore(timeMin)) timeMin = time;
					if (timeMax == null || !time.isBefore(timeMax)) timeMax = time;
				}

				final long between = ChronoUnit.HOURS.between(timeMin, timeMax);

				if (between <= 6L) {
					points.addAll(getReportPointsBy30mins(startDate, clickerPoints, timeMin, timeMax));
				} else if (between <= 12L) {
					points.addAll(getReportPointsByHour(clickerPoints, startDate, timeMin, timeMax));
				} else {
					points.addAll(getReportPointsBy2Hours(startDate, clickerPoints, timeMin, timeMax));
				}
			}
		} else if (ChronoUnit.DAYS.between(startDate, endDate) <= 12L) {
			List<ClickerPoint> clickerPoints = getClickerPointsBetweenDates(venueId, startDate, endDate);
			points.addAll(getReportPointsByDays(clickerPoints, startDate, endDate));

		} else if (ChronoUnit.WEEKS.between(startDate, endDate) <= 12L) {
			List<ClickerPoint> clickerPoints = getClickerPointsBetweenDates(venueId, startDate, endDate);
			points.addAll(getReportPointsByWeeks(clickerPoints, startDate, endDate));

		} else if (ChronoUnit.MONTHS.between(startDate, endDate) <= 12L) {
			List<ClickerPoint> clickerPoints = getClickerPointsBetweenDates(venueId, startDate, endDate);
			points.addAll(getReportPointsByDimension(clickerPoints, startDate, endDate, ChronoUnit.MONTHS));

		} else if (ChronoUnit.YEARS.between(startDate, endDate) <= 12L) {
			List<ClickerPoint> clickerPoints = getClickerPointsBetweenDates(venueId, startDate, endDate);
			points.addAll(getReportPointsByDimension(clickerPoints, startDate, endDate, ChronoUnit.YEARS));

		} else {
			return status(NOT_IMPLEMENTED);
		}

		return ok(Json.toJson(points));
	}

	private List<ClickerReportPoint> getReportPointsByDimension(final List<ClickerPoint> clickerPoints,
																final LocalDate startDate, final LocalDate endDate,
																final ChronoUnit dimension) {
		Map<LocalDate, ClickerReportPoint> pointMap = new HashMap<>();
		final long periods = dimension.between(startDate, endDate);

		for (int i = 0; i <= periods; i++) {
			final LocalDate key = startDate.plusMonths(i);
			pointMap.put(key, new ClickerReportPoint(key, LocalTime.MIDNIGHT, 0L, 0L));
		}

		clickerPoints.stream().forEach(clickerPoint -> {
			LocalDate date = clickerPoint.getClickerPointPk().getDate();
			ClickerReportPoint closestWeekPoint = null;
			for (ClickerReportPoint point : pointMap.values()) {
				if (closestWeekPoint == null || Math.abs(dimension.between(point.getLocalDate(), date)) <
						Math.abs(dimension.between(point.getLocalDate(), closestWeekPoint.getLocalDate()))) {
					closestWeekPoint = point;
				}
			}
			closestWeekPoint.setMen(closestWeekPoint.getMen() + clickerPoint.getMen());
			closestWeekPoint.setWoman(closestWeekPoint.getWoman() + clickerPoint.getWomen());
		});
		return new ArrayList<>(pointMap.values());
	}

	private List<ClickerReportPoint> getReportPointsByWeeks(List<ClickerPoint> clickerPoints, LocalDate startDate, LocalDate endDate) {
		Map<LocalDate, ClickerReportPoint> pointMap = new HashMap<>();
		final long periods = ChronoUnit.WEEKS.between(startDate, endDate);

		for (int i = 0; i <= periods; i++) {
			final LocalDate key = startDate.plusWeeks(i);
			pointMap.put(key, new ClickerReportPoint(key, LocalTime.MIDNIGHT, 0L, 0L));
		}

		clickerPoints.stream().forEach(clickerPoint -> {
			LocalDate date = clickerPoint.getClickerPointPk().getDate();

			for (ClickerReportPoint point : pointMap.values()) {
				final long between = ChronoUnit.DAYS.between(point.getLocalDate(), date);
				if (between >= 0 && between < 7) {
					point.setMen(point.getMen() + clickerPoint.getMen());
					point.setWoman(point.getWoman() + clickerPoint.getWomen());
					break;
				}
			}
		});
		return new ArrayList<>(pointMap.values());
	}

	private List<ClickerPoint> getClickerPointsBetweenDates(@ApiParam(required = true) Long venueId, LocalDate startDate, LocalDate endDate) {
		return ClickerPoint.find.where()
				.eq("venue_id", venueId)
				.ge("date", startDate)
				.le("date", endDate).findList();
	}

	private List<ClickerReportPoint> getReportPointsByDays(List<ClickerPoint> clickerPoints, LocalDate startDate, LocalDate endDate) {
		Map<LocalDate, ClickerReportPoint> pointMap = new HashMap<>();
		final long periods = ChronoUnit.DAYS.between(startDate, endDate);
		for (long i = 0; i <= periods; i++) {
			final LocalDate key = startDate.plusDays(i);
			pointMap.put(key, new ClickerReportPoint(key, LocalTime.MIDNIGHT, 0L, 0L));
		}

		clickerPoints.stream().forEach(clickerPoint -> {
			LocalDate date = clickerPoint.getClickerPointPk().getDate();
			final ClickerReportPoint point = pointMap.get(date);
			point.setMen(point.getMen() + clickerPoint.getMen());
			point.setWoman(point.getWoman() + clickerPoint.getWomen());
		});
		return new ArrayList<>(pointMap.values());
	}

	private List<ClickerReportPoint>  getReportPointsBy30mins(LocalDate startDate, List<ClickerPoint> clickerPoints, LocalTime timeMin, LocalTime timeMax) {
		Map<LocalTime, ClickerReportPoint> pointMap = new HashMap<>();
		//Инициализируем массив данных, для того, чтобы можно было отобразить нули на графике
		final long periods = ChronoUnit.MINUTES.between(timeMin, timeMax) / 30;
		for (int i = 0; i <= periods; i++) {
			final LocalTime key = timeMin.plusMinutes(i * 30);
			pointMap.put(key, new ClickerReportPoint(startDate, key, 0L, 0L));
		}

		clickerPoints.stream().forEach(clickerPoint -> {
			final ClickerReportPoint point = pointMap.get(clickerPoint.getClickerPointPk().getTime());
			point.setMen(point.getMen() + clickerPoint.getMen());
			point.setWoman(point.getWoman() + clickerPoint.getWomen());
		});
		return new ArrayList<>(pointMap.values());
	}

	private List<ClickerReportPoint> getReportPointsBy2Hours(LocalDate startDate, List<ClickerPoint> clickerPoints, LocalTime timeMin, LocalTime timeMax) {
		Map<LocalTime, ClickerReportPoint> pointMap = new HashMap<>();
		final long periods = ChronoUnit.HOURS.between(timeMin, timeMax) / 2;

		final int rest = timeMin.getHour() % 2;
		timeMin = timeMin.minusMinutes(timeMin.getMinute()).minusHours(rest);

		for (int i = 0; i <= periods; i++) {
			final LocalTime key = timeMin.plusHours(2L);
			pointMap.put(key, new ClickerReportPoint(startDate, key, 0L, 0L));
		}

		clickerPoints.stream().forEach(clickerPoint -> {
			LocalTime time = clickerPoint.getClickerPointPk().getTime();
			final int rest2 = time.getHour() % 2;
			time = time.minusMinutes(time.getMinute()).minusHours(rest2);

			final ClickerReportPoint point = pointMap.get(time);
			point.setMen(point.getMen() + clickerPoint.getMen());
			point.setWoman(point.getWoman() + clickerPoint.getWomen());
		});
		return new ArrayList<>(pointMap.values());
	}

	private List<ClickerReportPoint> getReportPointsByHour(List<ClickerPoint> clickerPoints, LocalDate startDate, LocalTime timeMin, LocalTime timeMax) {
		Map<LocalTime, ClickerReportPoint> pointMap = new HashMap<>();
		final long periods = ChronoUnit.HOURS.between(timeMin, timeMax);

		timeMin = timeMin.minusMinutes(timeMin.getMinute());
		for (long i = 0; i <= periods; i++) {
			final LocalTime key = timeMin.plusHours(i);
			pointMap.put(key, new ClickerReportPoint(startDate, key, 0L, 0L));
		}

		clickerPoints.stream().forEach(clickerPoint -> {
			LocalTime time = clickerPoint.getClickerPointPk().getTime();
			time = time.minusMinutes(time.getMinute());
			final ClickerReportPoint point = pointMap.get(time);
			point.setMen(point.getMen() + clickerPoint.getMen());
			point.setWoman(point.getWoman() + clickerPoint.getWomen());
		});

		return new ArrayList<>(pointMap.values());
	}

	private class InputParameters {
		private String startDate;
		private String endDate;
		private Long venueId;
		private String reportType;
		private ReportType reportTypeValue;

		private LocalDate startDateValue;
		private LocalDate endDateValue;

		/**
		 * Input parameters for the reports
		 * @param startDate required only if reportType equals INTERVAL
		 */
		InputParameters(String startDate, String endDate, Long venueId, String reportType) {
			this.startDate = startDate;
			this.endDate = endDate;
			this.venueId = venueId;
			this.reportType = reportType;
		}

		LocalDate getStartDateValue() {
			return startDateValue;
		}

		LocalDate getEndDateValue() {
			return endDateValue;
		}

		public InputParameters invoke() throws IllegalInputParameterStateException {
			if (StringUtils.isEmpty(endDate) || StringUtils.isEmpty(reportType) || venueId == null) {
				Logger.warn("Bad report params: " + this.toString());
				throw new IllegalInputParameterStateException("dateTo, venueId and reportType are required params");
			}

			try {
				reportTypeValue = ReportType.valueOf(reportType);
			} catch (IllegalArgumentException e) {
				Logger.warn("Bad reportType param: " + reportType);
				throw new IllegalInputParameterStateException("Unknown reportType param");
			}

			if (reportTypeValue.equals(ReportType.INTERVAL) && StringUtils.isEmpty(startDate)) {
				Logger.warn("Empty dateFrom param for ReportType.INTERVAL");
				throw new IllegalInputParameterStateException("dateFrom param required for ReportType.INTERVAL");
			}

			try {
				endDateValue = LocalDate.parse(endDate);
				if (reportTypeValue.equals(ReportType.INTERVAL)) {
					startDateValue = LocalDate.parse(startDate);
				} else {
					switch (reportTypeValue) {
						case DAILY:
							startDateValue = endDateValue.minusDays(1);
						break;
						case WEEKLY:
							startDateValue = endDateValue.minusWeeks(1);
						break;
						case MONTHLY:
							startDateValue = endDateValue.minusMonths(1);
						break;
					}
				}
			} catch (Exception e) {
				Logger.warn("Bad dateFrom (" + startDate + ") or dateTo (" + endDate + ") params");
				throw new IllegalInputParameterStateException("Bad values of dateFrom or dateTo params");
			}
			return this;
		}

		@Override
		public String toString() {
			return "InputParameters{" +
					"startDate='" + startDate + '\'' +
					", endDate='" + endDate + '\'' +
					", venueId=" + venueId +
					", reportType='" + reportType + '\'' +
					", reportTypeValue=" + reportTypeValue +
					", startDateValue=" + startDateValue +
					", endDateValue=" + endDateValue +
					'}';
		}
	}
}
