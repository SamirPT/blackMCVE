(function () {
	'use strict';

	angular.module('mrblack.auth').controller('SignInController', ['$scope', '$state', 'User', 'Toastr', function($scope, $state, User, Toastr) {
		$scope.user = User.bindUser();
		$scope.spinner = true;

		$scope.logIn = function() {
			$scope.spinner = false;
			var urlPhone = $scope.user.phoneNumber.replace(/ /g, '');

			User.getCode({
				phoneNumber: urlPhone
			}).then(function(response) {
				$scope.spinner = true;
				$state.go('confirm');
			},function(response){
				$scope.spinner = true;
				Toastr.error(response.data.error, "Error");
			});
		};
	}]);
}());
