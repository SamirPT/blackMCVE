(function () {
	'use strict';

	angular.module('mrblack.main').controller('RolesModalCtrl', ['$scope', '$modalInstance', '$state', 'empl','roles', 'VenueService', 'Toastr', function ($scope, $modalInstance, $state, empl, roles, VenueService, Toastr) {
		$scope.empl = empl;
		$scope.roles = roles;
		$scope.preferred = {};

		var getRoles = function () {
			angular.forEach($scope.empl.preferredRoles, function(role){
				$scope.preferred[role] = role;
			});
		};
		getRoles();

		$scope.updateState = function() {
			var preferredRoles = [];
			angular.forEach($scope.preferred, function(value, key){
				if(value){
					preferredRoles.push(key);
				}
			});

			VenueService.updateState({
				id: $state.params.id
			},{
				userId: $scope.empl.id,
				preferredRoles: preferredRoles
			}).then(function(response){
				Toastr.success(response.response, "Success");
				$scope.cancel();
			},function(response){
				Toastr.error(response.data.error, "Error");
			});
		};

		$scope.discardRequest = function() {
			VenueService.discardRequest({
				id: $state.params.id,
				userId: $scope.empl.id
			},{}).then(function(response){
				Toastr.success(response.response, "Success");
				$scope.cancel();
			},function(response){
				Toastr.error(response.data.error, "Error");
			});
		};

		$scope.checkRole = function(role) {
			if($scope.preferred[role]){
				$scope.preferred[role] = '';
			} else {
				$scope.preferred[role] = role;
			}
		};

		$scope.cancel = function () {
			$modalInstance.dismiss('cancel');
		};
	}]);
}());
