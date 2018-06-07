(function () {
	'use strict';

	angular.module('mrblack.main').controller('ScheduleCtrl', ['$scope', '$state', 'VenueService', 'ScheduleService', 'Toastr', 'EventsService', 'DictService', '$modal', 'ReservationService', 'UtilService', function ($scope, $state, VenueService, ScheduleService, Toastr, EventsService, DictService, $modal, ReservationService, UtilService) {
		$scope.spinner = false;
		$scope.state = $state;
		$scope.currentEvent = EventsService.getCurrentEvent();
		$scope.date = new Date();
		$scope.permissions = VenueService.getPermissions();

		$scope.bottleService = {
			TABLE: 'Table',
			STANDUP: 'Standup',
			BAR: 'Bar'
		};
		$scope.groupType = {
			MIX: 'Mix',
			ALL_GIRLS: 'All Girls',
			UNKNOWN: 'Unknown',
			PROMO: 'Promo',
			ALL_GUYS: 'All Guys',
			OWNER: 'Owner'
		};

		$scope.resStatuses = DictService.getReservationStatuses();

		$scope.getSchedule = function() {
			ScheduleService.getSchedule({
				venueId: $state.params.id,
				eventId: $scope.currentEvent.id
			}).then(function (response) {
				$scope.schedule = response;
				$scope.spinner = true;
			}, function (response) {
				Toastr.error(response.data.error, "Error");
				$scope.spinner = true;
			});
		};

		$scope.getEvents = function () {
			$scope.spinner = false;
			EventsService.getEvents({
				date: UtilService.formatRequestDate($scope.date),
				venueId: $state.params.id
			}).then(function (response) {
				$scope.events = response.eventsList;

				if($scope.events.length == 1) {
					EventsService.setCurrentEvent($scope.events[0]);
					$scope.currentEvent = EventsService.getCurrentEvent();
					$scope.getSchedule();
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
		};
		$scope.getEvents();

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
			$scope.getSchedule();
		};

		$scope.changeReservationState = function (reservationId, state) {
			$scope.arriveSpinner = true;
			$scope.currentClicked = reservationId;
			ReservationService.state({
				id: reservationId,
				newState: state
			}).then(function (response) {
				$scope.arriveSpinner = false;
				$scope.getSchedule();
			}, function (response) {
				$scope.arriveSpinner = false;
				Toastr.error(response.data.error, "Error");
				$scope.getSchedule();
			});
		};

		$scope.getStatusOpacity = function (state) {
			if (state === 'COMPLETED' || state === 'RELEASED') {
				return 'opacityScheduleStatus';
			} else if (state === 'ARRIVED') {
				return 'arrivedScheduleStatus';
			}
		};

		$scope.checkOneCR = function (table, compliment) {
			if (compliment) {
				return table.complimentAll || table.complimentGirls || table.complimentGirlsQty || table.complimentGuys || table.complimentGuysQty;
			} else {
				return table.reducedAll || table.reducedGirls || table.reducedGirlsQty || table.reducedGuys || table.reducedGuysQty;
			}
		};
	}]);
}());
