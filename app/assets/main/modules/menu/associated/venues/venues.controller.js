(function () {
	'use strict';

	angular.module('mrblack.main').controller('RolesModalCtrl', ['$scope', '$modalInstance', '$state', 'VenueService', 'Toastr', function ($scope, $modalInstance, $state, VenueService, Toastr) {
		$scope.loading = true;
		$scope.venues = {
			venues: [],
			events: []
		};

		$scope.status = {
			null: {
				name: 'Not Added',
				colorClass: 'text-black'
			},
			PREVIOUS: {
				name: 'Not Added',
				colorClass: 'text-black'
			},
			REQUESTED: {
				name: 'Requested',
				colorClass: 'text-warning-dker'
			}
		};

		var getListVenues = function () {
			VenueService.getListVenues({
				id: $state.params.id
			}).then(function(response){
				splitVenues(response);
				$scope.loading = false;
			},function(response){
				Toastr.error(response.data.error, "Error");
				$scope.loading = false;
			});
		};
		getListVenues();

		var splitVenues = function (venues) {
			$scope.venues = {
				venues: [],
				events: []
			};
			angular.forEach(venues, function (item) {
				if (item.venueType === 'VENUE') {
					$scope.venues.venues.push(item);
				} else if (item.venueType === 'EVENT') {
					$scope.venues.events.push(item);
				}
			});
		};

		$scope.createPromoRequest = function (venue) {
			$scope.loading = true;
			VenueService.createPromoRequest({
				id: venue.id,
				promoterId: $state.params.id
			}).then(function(response){
				getListVenues();
			},function(response){
				Toastr.error(response.data.error, "Error");
				$scope.loading = false;
			});
		};

		$scope.deleteVenueRequest = function (venue) {
			$scope.loading = true;
			VenueService.deletePromoRequest({
				id: venue.id,
				promoterId: $state.params.id
			}).then(function(response){
				getListVenues();
			},function(response){
				Toastr.error(response.data.error, "Error");
				$scope.loading = false;
			});
		};

		$scope.cancel = function () {
			$modalInstance.dismiss('cancel');
		};
	}]);
}());
