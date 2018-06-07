(function () {
	'use strict';

	angular.module('mrblack.main').controller('LeftPanelCtrl', ['$scope', '$state', 'VenueService', 'Toastr', '$rootScope', 'UserProfileService', function ($scope, $state, VenueService, Toastr, $rootScope, UserProfileService) {
		$scope.isAdmin = false;
		$scope.smallScreenSettingsVisibility = false;
		$scope.permissions = VenueService.getPermissions();

		UserProfileService.getUser().then(function (response) {
			$scope.isAdmin = response.isAdmin;
		}, function (response) {
			Toastr.error(response.data.error, "Error");
		});

		$scope.venueSettingsToggle = function (id) {
			var box = document.getElementById('venueSettingsBox');

			if (box.style.display === 'block') {
				box.style.display = 'none';
			} else {
				box.style.display = 'block';
			}

			VenueService.utilSetVenueId(id);
		};
		
		$scope.toggleVenueSettings = function () {
			$scope.smallScreenSettingsVisibility = !$scope.smallScreenSettingsVisibility;		
		};

		var stateId = 0;
		$rootScope.$on('$stateChangeSuccess', function(){
			if($state.params.id && $state.params.id != stateId) {
				VenueService.getVenue({
					id: $state.params.id
				}).then(function(response){
					$scope.venue = response;
					VenueService.setIsPromo($scope.venue.venueType === 'PROMOTER');
				},function(response){
					Toastr.error(response.data.error, "Error");
				});

				VenueService.getMyVenues().then(function(response){
					angular.forEach(response, function (venue) {
						if(venue.id == $state.params.id) {
							VenueService.setPermissions(venue.preferredRoles);
						}
					});
				},function(response){
					Toastr.error(response.data.error, "Error");
				});

				stateId = $state.params.id;
			}
		});
	}]);
}());
