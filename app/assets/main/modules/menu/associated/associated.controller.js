(function () {
	'use strict';

	angular.module('mrblack.main').controller('AssociatedCtrl', ['$scope', 'VenueService', 'DictService', 'Toastr', '$state', '$modal', function ($scope, VenueService, DictService, Toastr, $state, $modal) {
		$scope.myVenues = {
			venues: [],
			events: []
		};

		var getAssociatedVenues = function () {
			VenueService.getAssociatedVenues({
				id: $state.params.id
			}).then(function(response){
				splitVenues(response);
			},function(response){
				Toastr.error(response.data.error, "Error");
			});
		};
		getAssociatedVenues();

		var splitVenues = function (venues) {
			$scope.myVenues = {
				venues: [],
				events: []
			};
			angular.forEach(venues, function (item) {
				if (item.venueType === 'VENUE') {
					$scope.myVenues.venues.push(item);
				} else if (item.venueType === 'EVENT') {
					$scope.myVenues.events.push(item);
				}
			});
		};

		DictService.getRoles().then(function(response){
			$scope.roles = response;
		}, function(response){
			Toastr.error(response.data.error, "Error");
		});

		$scope.deleteAssociatedVenue = function (venue) {
			VenueService.deletePromoRequest({
				id: venue.id,
				promoterId: $state.params.id
			}).then(function(response){
				getAssociatedVenues();
			},function(response){
				Toastr.error(response.data.error, "Error");
			});
		};

		$scope.addVenue = function () {
			var modalInstance = $modal.open({
				templateUrl: '/assets/main/modules/menu/associated/venues/venues.html',
				controller: 'RolesModalCtrl'
			});

			modalInstance.result.then(
				function () { getAssociatedVenues(); },
				function () { getAssociatedVenues(); }
			);
		};
	}]);
}());
