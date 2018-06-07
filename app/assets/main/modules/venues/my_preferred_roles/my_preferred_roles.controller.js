(function () {
	'use strict';

	angular.module('mrblack.main').controller('DetailedVenueCtrl', ['$scope', 'VenueService', 'UserProfileService', 'Toastr', 'DictService', '$state', '$stateParams',function ($scope, VenueService, UserProfileService, Toastr, DictService, $state, $stateParams) {
		$scope.venue = $stateParams.venue || $state.go('my_venues');
		$scope.roles = {};
		$scope.preferred = {};
		$scope.loading = true;

		UserProfileService.getUser().then(function(response){
			$scope.user = response;
			getRoles();
		}, function(response){
			Toastr.error(response.data.error, "Error");
		});

		var getRoles = function () {
			if ($scope.venue.venueType === 'PROMOTER') {
				$scope.roles = DictService.getPromoRoles();
				$scope.loading = false;
				if($scope.venue.requestStatus){
					angular.forEach($scope.venue.preferredRoles, function(role){
						$scope.preferred[role] = role;
					});
				/* Predefined roles by user.preferredRoles */
				/*} else {
					angular.forEach($scope.user.preferredRoles, function(role){
						$scope.preferred[role] = role;
					});*/
				}
			} else {
				DictService.getRoles().then(function(response){
					$scope.loading = false;
					$scope.roles = response;
					if($scope.venue.requestStatus){
						angular.forEach($scope.venue.preferredRoles, function(role){
							$scope.preferred[role] = role;
						});
					/* Predefined roles by user.preferredRoles */
					/*} else {
						angular.forEach($scope.user.preferredRoles, function(role){
							$scope.preferred[role] = role;
						});*/
					}
				}, function(response){
					$scope.loading = false;
					Toastr.error(response.data.error, "Error");
				});
			}
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

		$scope.checkRole = function(role) {
			if($scope.preferred[role]){
				$scope.preferred[role] = '';
			} else {
				$scope.preferred[role] = role;
			}
		};

		$scope.createRequest = function(id){
			var preferredRoles = [];
			angular.forEach($scope.preferred, function(value, key){
				if(value){
					preferredRoles.push(key);
				}
			});

			VenueService.createRequest({
				id: $scope.venue.id
			},{
				userId: $scope.user.id,
				preferredRoles: preferredRoles
			}).then(function(response){
				Toastr.success(response.response, "Success");
				$scope.venue.requestStatus = 'REQUESTED';
			}, function(response) {
				Toastr.error(response.data.error, "Error");
			});
		};

		$scope.discardRequest = function() {
			VenueService.discardRequest({
				id: $scope.venue.id
			},{
				userId: $scope.user.id
			}).then(function(response){
				Toastr.success(response.response, "Success");
				$scope.venue.requestStatus = null;
			}, function(response) {
				Toastr.error(response.data.error, "Error");
			});
		};
	}]);
}());