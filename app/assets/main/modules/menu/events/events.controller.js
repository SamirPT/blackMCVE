(function () {
	'use strict';

	angular.module('mrblack.main').controller('EventsCtrl', ['$scope', '$state', 'VenueService', 'EventsService', 'Toastr', '$modal', 'UtilService', function ($scope, $state, VenueService, EventsService, Toastr, $modal, UtilService) {
		$scope.state = $state;
		$scope.format = 'fullDate';
		$scope.spinner = false;
		$scope.currentEvent = EventsService.getCurrentEvent();
		$scope.date = $scope.currentEvent.date ? new Date($scope.currentEvent.date) : new Date();
		$scope.permissions = VenueService.getPermissions();

		$scope.dateOptions = {
			startingDay: 1,
			showWeeks: false
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
				$scope.spinner = true;
			});
		};

		$scope.getEvents = function(date) {
			$scope.spinner = false;
			EventsService.getEvents({
				date: UtilService.formatRequestDate(date),
				venueId: $state.params.id
			}).then(function (response) {
				$scope.events = response.eventsList;
				$scope.spinner = true;
			}, function (response) {
				if(response.data) {
					Toastr.error(response.data.error, "Error");
					$scope.spinner = true;
				}
			});
		};
		$scope.getEvents($scope.date);

		$scope.deleteEvent = function(id, $event) {
			$event.stopPropagation();
			$scope.spinner = false;
			EventsService.deleteEvent({
				id: id
			}).then(function (response) {
				$scope.getEvents($scope.date);
			}, function (response) {
				Toastr.error(response.data.error, "Error");
			});
		};

		$scope.detailedEvent = function (event, create) {
			var modalInstance = $modal.open({
				templateUrl: '/assets/main/modules/menu/events/detailed/detailed.events.html',
				controller: 'DetailedEventsCtrl',
				windowClass: 'assign-reservation',
				resolve: {
					event: function() {
						return event || {};
					},
					date: function() {
						return $scope.date;
					},
					create: function() {
						return create;
					}
				}
			});

			modalInstance.result.then(detailedEventCallBack, detailedEventCallBack);
		};

		var detailedEventCallBack = function () {
			$scope.getEvents($scope.date);
		};
	}]);
}());
