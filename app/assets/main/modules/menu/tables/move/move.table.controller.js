(function () {
	'use strict';

	angular.module('mrblack.main').controller('MoveTableCtrl', ['$scope', '$modalInstance', 'table', 'Toastr', 'ReservationService', '$state', 'date', 'EventsService', '$modal', 'UtilService', function ($scope, $modalInstance, table, Toastr, ReservationService, $state, date, EventsService, $modal, UtilService) {
		$scope.loading = false;
		$scope.state = $state;
		$scope.table = table;
		$scope.startDate = new Date();
		$scope.startDate.setDate($scope.startDate.getDate() - 1);
		$scope.moveDate = date;
		$scope.currentEvent = EventsService.getCurrentEvent();

		$scope.dateOptions = {
			startingDay: 1,
			showWeeks: false
		};

		$scope.open = function($event) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.opened = true;
		};

		$scope.cancel = function () {
			$scope.loading = false;
			$modalInstance.dismiss('cancel');
		};

		$scope.getEvents = function(date) {
			$scope.spinner = true;
			EventsService.getEvents({
				date: UtilService.formatRequestDate(date),
				venueId: $state.params.id
			}).then(function (response) {
				$scope.events = response.eventsList;
				if($scope.events.length == 1) {
					EventsService.setCurrentEvent($scope.events[0]);
					$scope.currentEvent = EventsService.getCurrentEvent();
					$scope.moveReservation();
				} else if ($scope.events.length > 1){
					$scope.chooseEvent($scope.events, date);
				} else {
					Toastr.error('There is no events at this date', "Error");
					$scope.spinner = false;
				}
			}, function (response) {
				if(response.data) {
					Toastr.error(response.data.error, "Error");
					$scope.spinner = false;
				}
			});
		};

		$scope.chooseEvent = function (events, date) {
			var modalInstance = $modal.open({
				templateUrl: '/assets/main/modules/menu/modal/event/event.modal.html',
				controller: 'EventModalCtrl',
				windowClass: 'move-tables',
				resolve: {
					events: function() {
						return events || [];
					},
					date: function() {
						return date;
					}
				}
			});

			modalInstance.result.then($scope.moveReservation, $scope.moveReservation);
		};

		$scope.moveReservation = function () {
			var formattedDate = UtilService.formatRequestDate($scope.moveDate);

			ReservationService.move({
				id: table.reservationInfo.id,
				newDate: formattedDate,
				eventId: $scope.currentEvent.id
			}).then(function (response) {
				Toastr.success("Reservation moved to " + formattedDate, "Error");
				$scope.spinner = false;
				$scope.cancel();
			}, function (response) {
				$scope.spinner = false;
				Toastr.error(response.data.error, "Error");
			});
		};
	}]);
}());
