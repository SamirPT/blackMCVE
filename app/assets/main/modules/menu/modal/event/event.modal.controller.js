(function () {
	'use strict';

	angular.module('mrblack.main').controller('EventModalCtrl', ['$scope', '$modalInstance', '$state', 'events', 'date', 'EventsService', 'UtilService', function ($scope, $modalInstance, $state, events, date, EventsService, UtilService) {
		$scope.spinner = true;
		$scope.events = events;
		$scope.currentEvent = EventsService.getCurrentEvent();

		var getEventsList = function (date) {
			if($scope.events || $scope.events.length === 0) {
				$scope.spinner = true;
				EventsService.getEvents({
					date: UtilService.formatRequestDate(date),
					venueId: $state.params.id
				}).then(function (response) {
					$scope.events = response.eventsList;
					$scope.spinner = false;
				}, function (response) {
					if(response.data) {
						Toastr.error(response.data.error, "Error");
						$scope.spinner = false;
					}
				});
			} else {
				$scope.spinner = false;
			}
		};
		getEventsList(date);

		$scope.cancel = function () {
			$scope.loading = false;
			$modalInstance.dismiss('cancel');
		};

		$scope.selectEvent = function (event) {
			EventsService.setCurrentEvent(event);
			$scope.cancel();
		};
	}]);
}());
