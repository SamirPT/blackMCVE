(function () {
	'use strict';

	angular.module('mrblack.main').controller('EODCtrl', ['$scope', '$state', 'VenueService', 'TablesService', 'ReservationService', 'DictService', 'EventsService', 'Toastr', '$modal', 'UtilService', function ($scope, $state, VenueService, TablesService, ReservationService, DictService, EventsService, Toastr, $modal, UtilService) {
		$scope.state = $state;
		$scope.format = 'fullDate';
		$scope.spinner = false;
		$scope.dateOptions = {
			startingDay: 1,
			showWeeks: false
		};
		$scope.currentEvent = EventsService.getCurrentEvent();
		$scope.date = $scope.currentEvent.date ? new Date($scope.currentEvent.date) : new Date();
		$scope.events = [];
		$scope.permissions = VenueService.getPermissions();
		$scope.tables = [];
		$scope.groupType = {
			MIX: 'Mix',
			ALL_GIRLS: 'All Girls',
			UNKNOWN: 'Unknown',
			PROMO: 'Promo',
			ALL_GUYS: 'All Guys',
			OWNER: 'Owner'
		};

		$scope.bottleService = {
			TABLE: '#',
			STANDUP: 'S-',
			BAR: 'B-'
		};
		$scope.reservationStatuses = DictService.getReservationStatuses();

		$scope.statusColor = {
			ARRIVED: 'arrived',
			NO_SHOW: 'no-show',
			APPROVED: 'unassigned',
			PRE_RELEASED: 'released',
			RELEASED: 'released',
			COMPLETED: 'completed',
			CONFIRMED_COMPLETE: 'finalized'
		};

		$scope.getEODStatement = function (date) {
			VenueService.getEODStatement({
				id: $scope.state.params.id,
				eventId: $scope.currentEvent.id,
				date: UtilService.formatRequestDate(date)
			}).then(function (response) {
				$scope.tables = response.endOfDayItems;
				$scope.spinner = true;
			}, function (error) {
				Toastr.error(error.data.error, "Error");
				$scope.spinner = true;
			});
		};

		$scope.open = function($event) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.opened = true;
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
						$scope.getEODStatement($scope.date);
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
				$scope.getEODStatement($scope.date);
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
				$scope.getEODStatement($scope.date);
			} else {
				$scope.getEvents($scope.date);
			}
			$scope.spinner = true;
		};

		$scope.changeReservationState = function (reservationId, state, event) {
			event.stopPropagation();
			$scope.arriveSpinner = true;
			$scope.currentClicked = reservationId;
			ReservationService.state({
				id: reservationId,
				newState: state
			}).then(function (response) {
				$scope.arriveSpinner = false;
				$scope.getEODStatement($scope.date);
			}, function (response) {
				$scope.arriveSpinner = false;
				Toastr.error(response.data.error, "Error");
				$scope.getEODStatement($scope.date);
			});
		};

		var getRoles = function () {
			DictService.getRoles().then(function(response){
				$scope.roles = response;
			}, function(response){
				Toastr.error(response.data.error, "Error");
			});
		};
		getRoles();

		$scope.unassignStaffBatch = function (reservationId, staff, event) {
			event.stopPropagation();
			ReservationService.unassignStaffBatch({
				id: reservationId
			},[{
				id: staff.id,
				role: staff.role
			}]).then(function (response) {
				$scope.getEODStatement($scope.date);
			}, function (response) {
				Toastr.error(response.data.error, "Error");
			});
		};

		$scope.reservationDetails = function (reservation) {
			ReservationService.getReservation(reservation.id).then(
				function successCallback(currentReservation) {

					var modalInstance = $modal.open({
						templateUrl: '/assets/main/modules/menu/reservations/reservation_details/reservation_details.html',
						controller: 'ReservationDetailsCtrl',
						windowClass: 'res-details-modal',
						resolve: {
							reservation: function() {
								return currentReservation;
							},
							reservationDate: function () {
								return $scope.currentReservationDate;
							},
							eventId: function () {
								return $scope.currentEvent.id;
							},
							table: {}
						}
					});

					modalInstance.result.then(
						function () { $scope.getEODStatement($scope.date); },
						function () { $scope.getEODStatement($scope.date); }
					);
				},
				function errorCallback() {
					Toastr.error('Reservation loading failed', 'Error');
				}
			);
		};

		$scope.completeReservation = function (reservation, $event) {
			$event.stopPropagation();
			var modalInstance = $modal.open({
				templateUrl: '/assets/main/modules/menu/reservations/complete/complete.html',
				controller: 'CompleteResCtrl',
				resolve: {
					permission: function () {
						return true;
					},
					reservation: function () {
						return reservation;
					}
				}
			});

			modalInstance.result.then(
				function () { $scope.getEODStatement($scope.date); },
				function () { $scope.getEODStatement($scope.date); }
			);
		};

		$scope.formattedTime = function (time) {
			return time.slice(0, 5);
		};
	}]);
}());
