(function () {
	'use strict';

	angular.module('mrblack.main').controller('TablesCtrl', ['$scope', '$state', 'VenueService', 'TablesService', 'ReservationService', 'DictService', 'EventsService', 'Toastr', '$modal', 'UtilService', function ($scope, $state, VenueService, TablesService, ReservationService, DictService, EventsService, Toastr, $modal, UtilService) {
		$scope.state = $state;
		$scope.format = 'fullDate';
		$scope.spinner = false;
		$scope.dateOptions = {
			startingDay: 1,
			showWeeks: false
		};
		$scope.currentTable = {};
		$scope.currentEvent = EventsService.getCurrentEvent();
		$scope.date = $scope.currentEvent.date ? new Date($scope.currentEvent.date) : new Date();
		$scope.events = [];
		$scope.permissions = VenueService.getPermissions();
		$scope.groupType = {
			MIX: 'Mix',
			ALL_GIRLS: 'All Girls',
			UNKNOWN: 'Unknown',
			PROMO: 'Promo',
			ALL_GUYS: 'All Guys',
			OWNER: 'Owner'
		};

		$scope.bottleService = {
			TABLE: 'Table',
			STANDUP: 'Standup',
			BAR: 'Bar'
		};

		$scope.open = function($event) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.opened = true;
		};

		var splitByAssignments = function (value, type) {
			if (!!value.reservationInfo) {
				$scope.tables[type].assigned.push(value);
			} else if (value.tableInfo.closed) {
				$scope.tables[type].closed.push(value);
			} else {
				$scope.tables[type].opened.push(value);
			}
		};

		var splitTables = function(response){
			$scope.tables = {
				tables: {
					opened: [],
					assigned: [],
					closed: []
				},
				standups:  {
					opened: [],
					assigned: [],
					closed: []
				},
				bars:  {
					opened: [],
					assigned: [],
					closed: []
				}
			};
			angular.forEach(response, function(value, key){
				if(value.tableInfo.bottleServiceType == 'TABLE') {
					splitByAssignments(value, 'tables');
				} else if(value.tableInfo.bottleServiceType == 'STANDUP') {
					splitByAssignments(value, 'standups');
				} else {
					splitByAssignments(value, 'bars');
				}
			});
		};

		$scope.getSeating = function(date) {
			TablesService.getSeating({
				venueId: $state.params.id,
				date: UtilService.formatRequestDate(date),
				eventId: $scope.currentEvent.id
			}).then(function (response) {
				$scope.tables = response;
				splitTables(response);
				$scope.spinner = true;
			}, function (response) {
				if(response.data) {
					Toastr.error(response.data.error, "Error");
					$scope.spinner = true;
				}
			});
		};

		$scope.getNextEventDate = function (date) {
			VenueService.getNextEventDate({
				id: $state.params.id,
				from: UtilService.formatRequestDate(date)
			}).then(function (response) {
				$scope.date = new Date(response.date);
				$scope.getEvents($scope.date);
			}, function (response) {
				Toastr.error(response.data.error, "Error");
				$scope.date = $scope.currentEvent.date ? new Date($scope.currentEvent.date) : new Date();
				$scope.spinner = true;
			});
		};

		$scope.getPrevEventDate = function (date) {
			VenueService.getPrevEventDate({
				id: $state.params.id,
				from: UtilService.formatRequestDate(date)
			}).then(function (response) {
				$scope.date = new Date(response.date);
				$scope.getEvents($scope.date);
			}, function (response) {
				Toastr.error(response.data.error, "Error");
				$scope.date = $scope.currentEvent.date ? new Date($scope.currentEvent.date) : new Date();
				$scope.spinner = true;
			});
		};

		$scope.getEvents = function (date) {
			$scope.spinner = false;
			var requestDate = UtilService.formatRequestDate(date);
			if(!$scope.currentEvent.id || $scope.currentEvent.date != requestDate) {
				EventsService.getEvents({
					date: requestDate,
					venueId: $state.params.id
				}).then(function (response) {
					$scope.events = response.eventsList;

					if($scope.events.length == 1) {
						EventsService.setCurrentEvent($scope.events[0]);
						$scope.currentEvent = EventsService.getCurrentEvent();
						$scope.getSeating($scope.date);
					} else if ($scope.events.length > 1){
						$scope.chooseEvent();
					} else {
						$scope.spinner = true;
					}
				}, function (response) {
					if(response.data) {
						Toastr.error(response.data.error, "Error");
						$scope.spinner = true;
					}
				});
			} else {
				$scope.events.push(EventsService.getCurrentEvent());
				$scope.date = new Date($scope.currentEvent.date);
				$scope.getSeating($scope.date);
			}
		};
		$scope.getEvents($scope.date);

		$scope.chooseEvent = function () {
			var modalInstance = $modal.open({
				templateUrl: '/assets/main/modules/menu/modal/event/event.modal.html',
				controller: 'EventModalCtrl',
				windowClass: 'move-tables',
				resolve: {
					events: function() {
						return $scope.events || [];
					},
					date: function() {
						return $scope.date;
					}
				}
			});

			modalInstance.result.then(chooseEventCallBack, chooseEventCallBack);
		};

		var chooseEventCallBack = function () {
			if($scope.currentEvent.date) {
				$scope.date = new Date($scope.currentEvent.date);
				$scope.getSeating($scope.date);
			} else {
				$scope.getEvents($scope.date);
			}
			$scope.spinner = true;
		};

		$scope.editTables = function () {
			var modalInstance = $modal.open({
				templateUrl: '/assets/main/modules/menu/tables/edit/edit.table.html',
				controller: 'EditTableCtrl',
				windowClass: 'assign-reservation',
				resolve: {
					tables: function () {
						return $scope.tables;
					},
					date: function () {
						return $scope.date;
					}
				}
			});

			modalInstance.result.then(chooseEventCallBack, chooseEventCallBack);
		};

		var getRoles = function () {
			DictService.getRoles().then(function(response){
				$scope.roles = response;
			}, function(response){
				Toastr.error(response.data.error, "Error");
			});
		};
		getRoles();

		$scope.unassignStaffBatch = function (reservationId, staff) {
			ReservationService.unassignStaffBatch({
				id: reservationId
			},[{
				id: staff.id,
				role: staff.role
			}]).then(function (response) {
				$scope.getSeating($scope.date);
			}, function (response) {
				Toastr.error(response.data.error, "Error");
			});
		};

		$scope.unassignTable = function (reservationId) {
			ReservationService.unassignTable({
				id: reservationId
			}).then(function (response) {
				$scope.getSeating($scope.date);
			}, function (response) {
				Toastr.error(response.data.error, "Error");
			});
		};

		$scope.guests = function (reservationId, gender, action) {
			$scope.guestSpinner = true;
			$scope.currentClicked = reservationId;
			ReservationService.guests({
				id: reservationId,
				action: action,
				gender: gender
			}).then(function (response) {
				$scope.getSeating($scope.date);
				$scope.guestSpinner = false;
			}, function (response) {
				Toastr.error(response.data.error, "Error");
				$scope.getSeating($scope.date);
				$scope.guestSpinner = false;
			});
		};

		$scope.changeReservationState = function (reservationId, state) {
			$scope.arriveSpinner = true;
			$scope.currentClicked = reservationId;
			ReservationService.state({
				id: reservationId,
				newState: state
			}).then(function (response) {
				$scope.arriveSpinner = false;
				$scope.getSeating($scope.date);
			}, function (response) {
				$scope.arriveSpinner = false;
				Toastr.error(response.data.error, "Error");
				$scope.getSeating($scope.date);
			});
		};

		$scope.createReservation = function (table, reservation) {
			var modalInstance = $modal.open({
				templateUrl: '/assets/main/modules/menu/reservations/reservation_details/reservation_details.html',
				controller: 'ReservationDetailsCtrl',
				windowClass: 'res-details-modal',
				resolve: {
					reservation: function() {
						return reservation;
					},
					reservationDate: function () {
						return UtilService.formatRequestDate($scope.date);
					},
					eventId: function () {
						return $scope.currentEvent.id;
					},
					table: function () {
						return table;
					}
				}
			});

			modalInstance.result.then(
				function () { $scope.getSeating($scope.date); },
				function () { $scope.getSeating($scope.date); }
			);
		};

		var menuCallBack = function () {
			$scope.date = new Date($scope.currentEvent.date);
			$scope.getSeating($scope.date);
		};

		$scope.menu = function (item, table) {
			var menuItems = {
				assignTable: {
					templateUrl: '/assets/main/modules/menu/tables/assign/assign.table.html',
					controller: 'AssignTableCtrl',
					windowClass: 'assign-reservation'
				},
				assignStaff: {
					templateUrl: '/assets/main/modules/menu/tables/staff/staff.table.html',
					controller: 'StaffTableCtrl',
					windowClass: 'big-modal'
				},
				moveReservation: {
					templateUrl: '/assets/main/modules/menu/tables/move/move.table.html',
					controller: 'MoveTableCtrl',
					windowClass: 'move-tables'
				}
			};

			var modalInstance = $modal.open({
				templateUrl: menuItems[item].templateUrl,
				controller: menuItems[item].controller,
				windowClass: menuItems[item].windowClass,
				resolve: {
					table: function() {
						return table;
					},
					date: function() {
						return $scope.date;
					}
				}
			});

			modalInstance.result.then(menuCallBack, menuCallBack);
		};

		$scope.closeTable = function (table, close) {
			var requestDate = UtilService.formatRequestDate($scope.date);
			if (close) {
				TablesService.closeTable({
					id: table.tableInfo.id,
					date: requestDate
				}).then(function (response) {
					$scope.getSeating($scope.date);
				}, function (response) {
					Toastr.error(response.data.error, "Error");
				});
			} else {
				TablesService.openTable({
					id: table.tableInfo.id,
					date: requestDate
				}).then(function (response) {
					$scope.getSeating($scope.date);
				}, function (response) {
					Toastr.error(response.data.error, "Error");
				});
			}
		};

		$scope.checkOneCR = function (table, compliment) {
			if (compliment) {
				return table.reservationInfo.complimentAll || table.reservationInfo.complimentGirls || table.reservationInfo.complimentGirlsQty || table.reservationInfo.complimentGuys || table.reservationInfo.complimentGuysQty;
			} else {
				return table.reservationInfo.reducedAll || table.reservationInfo.reducedGirls || table.reservationInfo.reducedGirlsQty || table.reservationInfo.reducedGuys || table.reservationInfo.reducedGuysQty;
			}
		};
	}]);
}());
