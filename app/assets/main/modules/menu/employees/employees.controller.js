(function () {
	'use strict';

	angular.module('mrblack.main').controller('EmployeesCtrl', ['$scope', '$state', 'VenueService', 'DictService', 'Toastr', '$stateParams', '$modal', function ($scope, $state, VenueService, DictService, Toastr, $stateParams, $modal) {
		$scope.people = {};

		DictService.getRoles().then(function(response){
			$scope.roles = response;
			$scope.promoRoles = {
				PROMOTER: 'Promoter',
				MANAGER: 'Manager'
			};
		}, function(response){
			Toastr.error(response.data.error, "Error");
		});

		var getEmployees = function () {
			VenueService.getReqEmpl({
				id: $stateParams.id
			},{}).then(function(response){
				$scope.people = response;
				$scope.isPromo = VenueService.getIsPromo();
			}, function(response){
				Toastr.error(response.data.error, "Error");
			});
		};
		getEmployees();

		$scope.updatePromoRequest = function (promo) {
			VenueService.updatePromoRequest({
				id: $stateParams.id,
				promoterId: promo.id
			}).then(function(response){
				getEmployees();
			},function(response){
				Toastr.error(response.data.error, "Error");
			});
		};

		$scope.deletePromoRequest = function (promo) {
			VenueService.deletePromoRequest({
				id: $stateParams.id,
				promoterId: promo.id
			}).then(function(response){
				getEmployees();
			},function(response){
				Toastr.error(response.data.error, "Error");
			});
		};

		$scope.checkPrevious = function (status) {
			return status === 'PREVIOUS';
		};

		$scope.changeRoles = function (empl) {
			var modalInstance = $modal.open({
				templateUrl: '/assets/main/modules/menu/employees/roles/roles.modal.html',
				controller: 'RolesModalCtrl',
				resolve: {
					empl: function () {
						return empl;
					},
					roles: function () {
						return VenueService.getIsPromo() ? $scope.promoRoles : $scope.roles;
					}
				}
			});

			modalInstance.result.then(
				function () { getEmployees(); },
				function () { getEmployees(); }
			);
		};
	}]);
}());
