(function () {
	'use strict';

	angular.module('mrblack.main').controller('DetailedEventsCtrl', ['$scope', '$modalInstance', 'Toastr', '$state', 'date', 'event', 'create', 'EventsService', 'VenueService', 'Cropper', '$modal', 'UtilService', function ($scope, $modalInstance, Toastr, $state, date, event, create, EventsService, VenueService, Cropper, $modal, UtilService) {
		$scope.create = create;
		$scope.event = event;
		$scope.event.date = date;
		$scope.format = 'fullDate';
		$scope.spinner = false;
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

		$scope.cancel = function () {
			$scope.loading = false;
			$modalInstance.dismiss('cancel');
		};

		$scope.onFile = function(blob) {
			var file;
			Cropper.encode((file = blob)).then(function(dataUrl) {
				var modalInstance = $modal.open({
					templateUrl: '/assets/main/modules/menu/events/modal_cropper/modal_cropper.html',
					controller: 'EventsModalCtrl',
					resolve: {
						file: function () {
							return file;
						},
						dataUrl: function() {
							return dataUrl;
						},
						event: function () {
							return $scope.event;
						}
					}
				});
			});
		};

		$scope.actionEvent = function() {
			$scope.spinner = true;
			var event = {
				id: $scope.event.id,
				name: $scope.event.name,
				description: $scope.event.description || "",
				fbEventUrl: $scope.event.fbEventUrl || "",
				startsAt: $scope.event.startsAt,
				endsAt: $scope.event.endsAt,
				date: UtilService.formatRequestDate(date),
				venueId: parseInt($state.params.id),
				repeatable: $scope.event.repeatable || false,
				pictureUrl: $scope.event.pictureUrl
			};

			if($scope.create) {
				EventsService.createEvent({}, event).then(eventResponse, eventResponse);
			} else {
				EventsService.updateEvent({}, event).then(eventResponse, eventResponse);
			}
		};

		var eventResponse = function (response) {
			if(response.$resolved) {
				$scope.cancel();
			} else {
				Toastr.error(response.data.error, "Error");
			}
		};
	}]);
}());
