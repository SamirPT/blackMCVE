package controllers.api.v1;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import io.swagger.annotations.*;
import models.*;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import security.MrBlackUserProfile;
import to.*;
import util.ReservationsHelper;
import util.RolesHelper;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by arkady on 28/03/16.
 */
@Api(value = "/api/v1/tables", description = "Tables management", tags = "tables", basePath = "/api/v1/tables")
public class Tables extends UserProfileController<MrBlackUserProfile> {

	private ReservationsHelper reservationsHelper;

	@Inject
	public Tables(ReservationsHelper reservationsHelper) {
		this.reservationsHelper = reservationsHelper;
	}

	@ApiOperation(
			nickname = "closeTableForDate",
			value = "Closes table for date",
			notes = "Closes table for date",
			httpMethod = "PUT",
			consumes = "text/html"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Table with this id not found", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result closeTable(@ApiParam(required = true) Long tableId, @ApiParam(required = true) String dateString) {
		LocalDate date;
		try {
			date = LocalDate.parse(dateString);
		} catch (DateTimeParseException e) {
			Logger.warn("Can't parse date: " + dateString, e);
			return badRequest(Json.toJson(new ErrorMessage("Date and tableId should be consistent")));
		}

		final Place place = Place.find.fetch("venue").where().idEq(tableId).findUnique();

		if (place == null) {
			return notFound(Json.toJson(new ErrorMessage("Table with this id not found")));
		}

		final User currentUser = getUserProfile().getUser();
		if (!canManageTables(place.getVenue().getId(), currentUser)) {
			return forbidden(ErrorMessage.getJsonForbiddenMessage());
		}

		ClosedTablePk closedTablePk = new ClosedTablePk(place.getVenue().getId(), tableId, date);
		ClosedTable closedTable = ClosedTable.find.byId(closedTablePk);

		if (closedTable == null) {
			closedTable = new ClosedTable(closedTablePk);
			try {
				closedTable.save();
			} catch (Exception e) {
				Logger.warn("Can't close table", e);
				return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
			}
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "openTableForDate",
			value = "Open table for date",
			notes = "Open table for date",
			httpMethod = "PUT",
			consumes = "text/html"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Table with this id not found", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result openTable(@ApiParam(required = true) Long tableId, @ApiParam(required = true) String dateString) {
		LocalDate date;
		try {
			date = LocalDate.parse(dateString);
		} catch (DateTimeParseException e) {
			Logger.warn("Can't parse date: " + dateString, e);
			return badRequest(Json.toJson(new ErrorMessage("Date and tableId should be consistent")));
		}

		final Place place = Place.find.fetch("venue").where().idEq(tableId).findUnique();

		if (place == null) {
			return notFound(Json.toJson(new ErrorMessage("Table with this id not found")));
		}

		final User currentUser = getUserProfile().getUser();
		if (!canManageTables(place.getVenue().getId(), currentUser)) {
			return forbidden(ErrorMessage.getJsonForbiddenMessage());
		}

		ClosedTablePk closedTablePk = new ClosedTablePk(place.getVenue().getId(), tableId, date);
		ClosedTable closedTable = ClosedTable.find.byId(closedTablePk);

		if (closedTable != null) {
			try {
				closedTable.delete();
			} catch (Exception e) {
				Logger.warn("Can't close table", e);
				return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
			}
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "getTablesWithSeating",
			value = "Get tables with seating",
			notes = "Get tables with seating",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Tables with seating", response = to.TableWithSeating[].class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Table with this id not found", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getTablesSeating(@ApiParam(required = true) Long venueId,
								   @ApiParam(required = true) String dateString,
								   @ApiParam(required = true) Long eventId) {

		final User currentUser = getUserProfile().getUser();
		if (!canManageTables(venueId, currentUser)) {
			return forbidden(ErrorMessage.getJsonForbiddenMessage());
		}

		LocalDate date;
		try {
			date = LocalDate.parse(dateString);
		} catch (DateTimeParseException e) {
			Logger.warn("Can't parse date: " + dateString, e);
			return badRequest(Json.toJson(new ErrorMessage("Date and venueId should be consistent")));
		}

		List<Place> tablesList;
		try {
			tablesList = getSeatingPlaces(venueId);
		} catch (Exception e) {
			Logger.warn("Can't get tables for venue id=" + venueId, e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		List<Reservation> reservations;
		try {
			reservations = reservationsHelper.getReservationsWithBSAndTables(venueId, date, eventId);
		} catch (Exception e) {
			Logger.warn("Can't fetch reservations list", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		Map<Long, Reservation> reservationByTableIdMap;
		if (reservations != null) {
			try {
				reservationByTableIdMap = reservations.stream()
						.collect(Collectors.toMap(r -> r.getTable().getId(), r -> r));
			} catch (Exception e) {
				Logger.warn("Multiple reservations found. Date: " + dateString + ", venueId: " + venueId, e);
				return internalServerError(Json.toJson(new ErrorMessage("More than one reservation per table found")));
			}
		} else {
			reservationByTableIdMap = new HashMap<>();
		}

		List<ClosedTable> closedTables = Ebean.find(ClosedTable.class).where()
				.eq("venue_id", venueId)
				.eq("date", date)
				.findList();

		List<Long> closedTableIds = null;
		if (closedTables != null) {
			closedTableIds = closedTables.stream().map(closedTable -> closedTable.getClosedTablePk().getPlaceId())
					.collect(Collectors.toList());
		}

		List<TableWithSeating> tableWithSeatingList = new ArrayList<>();
		for (Place table : tablesList) {
			TableWithSeating tableWithSeating = new TableWithSeating();
			final TableInfo tableInfo = new TableInfo(table);
			tableInfo.setClosed(closedTableIds != null && closedTableIds.contains(table.getId()));
			tableWithSeating.setTableInfo(tableInfo);

			if (reservationByTableIdMap.containsKey(table.getId())) {
				Reservation reservation = reservationByTableIdMap.get(table.getId());

				final ReservationInfo reservationInfo = reservationsHelper.reservationToInfo(reservation, true);
				tableWithSeating.setReservationInfo(reservationInfo);
			}

			tableWithSeatingList.add(tableWithSeating);
		}

		return ok(Json.toJson(tableWithSeatingList));
	}

	private boolean canManageTables(@ApiParam(required = true) Long venueId, User currentUser) {
		if (currentUser.isAdmin()) return true;
		UserVenuePK key = new UserVenuePK(currentUser.getId(), venueId);
		UserVenue userVenue = UserVenue.finder.byId(key);
		return userVenue != null && RolesHelper.canViewAndManageTableView(userVenue.getRoles());
	}

	@ApiOperation(
			nickname = "deleteTable",
			value = "Delete seating place",
			notes = "Delete seating place",
			httpMethod = "DELETE"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 400, message = "Please check sent data", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Table with this id not found", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result deleteTable(Long id) {
		Place place = Place.find.fetch("venue").where().idEq(id).findUnique();
		if (place == null) {
			Logger.warn("Bad delete table request. Not found. id=" + id);
			return notFound(Json.toJson(new ErrorMessage("Table with this id not found")));
		}

		final User currentUser = getUserProfile().getUser();
		if (!canManageTables(place.getVenue().getId(), currentUser)) {
			return forbidden(ErrorMessage.getJsonForbiddenMessage());
		}

		try {
			place.delete();
		} catch (Exception e) {
			Logger.warn("Can't delete seating place", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}
		return ok(Json.toJson(new ResponseMessage("Ok")));
	}

	@ApiOperation(
			nickname = "updateTable",
			value = "Update seating place",
			notes = "Update seating place",
			httpMethod = "UPDATE"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 400, message = "Please check sent data", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Table with this id not found", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "table", value = "Table info", required = true, dataType = "to.TableInfo", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result updateTable() {
		JsonNode tableJson = request().body().asJson();
		TableInfo tableInfo;
		try {
			tableInfo = Json.fromJson(tableJson, TableInfo.class);
		} catch (Exception e) {
			Logger.warn("Bad request for update table: " + tableJson.asText());
			return badRequest(Json.toJson(new ErrorMessage("Please check sent data")));
		}

		Place place = Place.find.fetch("venue").where().idEq(tableInfo.getId()).findUnique();

		if (place == null) {
			Logger.warn("Bad update table request, place not found: " + tableInfo);
			return notFound(Json.toJson(new ErrorMessage("Table with this id not found")));
		}

		final User currentUser = getUserProfile().getUser();
		if (!canManageTables(place.getVenue().getId(), currentUser)) {
			return forbidden(ErrorMessage.getJsonForbiddenMessage());
		}

		if (tableInfo.getBottleServiceType() != null) place.setBottleServiceType(tableInfo.getBottleServiceType());
		if (tableInfo.getPlaceNumber() != null) place.setPlaceNumber(tableInfo.getPlaceNumber());

		try {
			place.update();
		} catch (Exception e) {
			Logger.warn("Can't update table", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage("Ok")));
	}

	@ApiOperation(
			nickname = "createTable",
			value = "Create seating place",
			notes = "Create seating place",
			httpMethod = "POST"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 400, message = "Please check sent data", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "table", value = "New table info", required = true, dataType = "to.CreateTableRequest", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result createTable() {
		JsonNode tableJson = request().body().asJson();
		CreateTableRequest tableInfo;
		try {
			tableInfo = Json.fromJson(tableJson, CreateTableRequest.class);
		} catch (Exception e) {
			Logger.warn("Bad request for creating table: " + tableJson.asText());
			return badRequest(Json.toJson(new ErrorMessage("Please check sent data")));
		}

		final User currentUser = getUserProfile().getUser();
		if (!canManageTables(tableInfo.getVenueId(), currentUser)) {
			return forbidden(ErrorMessage.getJsonForbiddenMessage());
		}

		Place place = new Place();
		place.setBottleServiceType(tableInfo.getBottleServiceType());
		place.setVenue(new Venue(tableInfo.getVenueId()));
		place.setPlaceNumber(tableInfo.getPlaceNumber());

		try {
			place.save();
		} catch (Exception e) {
			Logger.warn("Can't save new table", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage("Ok")));
	}

	@ApiOperation(
			nickname = "getTables",
			value = "Get seating places of venue",
			notes = "Get seating place list of venue ",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Tables list", response = to.TablesList.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getTables(Long venueId) {
		final User currentUser = getUserProfile().getUser();
		if (!canManageTables(venueId, currentUser)) {
			return forbidden(ErrorMessage.getJsonForbiddenMessage());
		}

		List<Place> tablesList;
		try {
			tablesList = getSeatingPlaces(venueId);
		} catch (Exception e) {
			Logger.warn("Can't get tables for venue id=" + venueId, e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		TablesList tables = new TablesList();

		if (tablesList != null) {
			for (Place place : tablesList) {
				TableInfo tableInfo = new TableInfo(place);
				tables.getTablesList().add(tableInfo);
			}
		}

		return ok(Json.toJson(tables));
	}

	private List<Place> getSeatingPlaces(Long venueId) {
		return Place.find.where().eq("venue.id", venueId).findList();
	}
}
