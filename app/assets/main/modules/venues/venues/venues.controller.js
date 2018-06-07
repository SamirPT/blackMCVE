(function () {
	'use strict';

	angular.module('mrblack.main').controller('VenuesCtrl', ['$scope', '$state', 'VenueService', 'Toastr', function ($scope, $state, VenueService, Toastr) {
		$scope.venues = {
			venues: [],
			events: [],
			promoters: []
		};

		VenueService.getVenues().then(function(response){
			splitVenues(response);
		},function(response){
			Toastr.error(response.data.error, "Error");
		});

		var splitVenues = function (venues) {
			angular.forEach(venues, function (item) {
				if (item.venueType === 'VENUE') {
					$scope.venues.venues.push(item);
				} else if (item.venueType === 'EVENT') {
					$scope.venues.events.push(item);
				} else {
					$scope.venues.promoters.push(item);
				}
			});
		};

		$scope.status = {
			null: {
				name: 'Not Added',
				colorClass: 'text-black'
			},
			REQUESTED: {
				name: 'Requested',
				colorClass: 'text-warning-dker'
			},
			APPROVED: {
				name: 'Approved',
				colorClass: 'text-success-dker'
			}
		};

		$scope.selectVenue = function(venue) {
			$state.go('my_preferred_roles', {venue: venue});
		};
	}]);
}());