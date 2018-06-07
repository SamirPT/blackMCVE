(function () {
	'use strict';

	angular.module('mrblack.main').controller('PromotionsCtrl', ['$scope', '$state', 'PromotionsService', 'Toastr', function ($scope, $state, PromotionsService, Toastr) {
		$scope.selectedUsers = {};
		$scope.promotion = {};

		var getFbUsers = function () {
			PromotionsService.getFbUsers({
				venueId: $state.params.id
			},{}).then(function (response) {
				$scope.users = response;
			}, function (response) {
				Toastr.error(response.data.error, 'Error');
			});
		};
		getFbUsers();

		$scope.selectUser = function (id) {
			if($scope.selectedUsers[id]){
				$scope.selectedUsers[id] = '';
			} else {
				$scope.selectedUsers[id] = id;
			}
		};

		$scope.submitPromoForm = function () {
			var selectedUsers = [];
			angular.forEach($scope.selectedUsers, function (value, key) {
				if(value) {
					selectedUsers.push(parseInt(key));
				}
			});

			PromotionsService.createFeedback({},{
				url: $scope.promotion.url,
				message: $scope.promotion.message,
				userIds: selectedUsers
			}).then(function (response) {
				Toastr.success('Post created', 'Success');
			}, function (response) {
				Toastr.error(response.data.error, 'Error');
			});
		};
	}]);
}());
