(function () {
	'use strict';

	angular.module('mrblack.main').controller('MyVenuesCtrl', ['$scope', 'VenueService', 'DictService', 'Toastr', '$state', 'EventsService', function ($scope, VenueService, DictService, Toastr, $state, EventsService) {
		$scope.myVenues = {
			venues: [],
			events: [],
			promoters: []
		};

		VenueService.getMyVenues().then(function(response){
			splitVenues(response);
		},function(response){
			Toastr.error(response.data.error, "Error");
		});

		var splitVenues = function (venues) {
			$scope.myVenues = {
				venues: [],
				events: [],
				promoters: []
			};
			angular.forEach(venues, function (item) {
				if (item.venueType === 'VENUE') {
					$scope.myVenues.venues.push(item);
				} else if (item.venueType === 'EVENT') {
					$scope.myVenues.events.push(item);
				} else {
					$scope.myVenues.promoters.push(item);
				}
			});
		};

		DictService.getRoles().then(function(response){
			$scope.roles = response;
			$scope.roles.PROMOTER = 'Promoter';
			$scope.promoRoles = {
				PROMOTER: 'Promoter',
				MANAGER: 'Manager'
			};
		}, function(response){
			Toastr.error(response.data.error, "Error");
		});

		$scope.changeRoles = function(venue, $event) {
			$event.stopPropagation();
			$state.go('my_preferred_roles', {venue: venue});
		};

		$scope.selectVenue = function(venue) {
			EventsService.clearCurrentEvent();
			VenueService.setIsPromo(venue.venueType === 'PROMOTER');
			if (venue.venueType === 'PROMOTER' && !venue.preferredRoles.includes('MANAGER')) {
				$state.go('my_preferred_roles', {venue: venue});
			} else if (venue.venueType === 'PROMOTER' && venue.preferredRoles.includes('MANAGER')) {
				$state.go('employees', {id: venue.id});
			} else {
				$state.go('venue_snapshot', {id: venue.id});
			}
		};
	}]);
}());
