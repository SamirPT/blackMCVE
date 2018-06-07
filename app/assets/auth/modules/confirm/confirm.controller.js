(function () {
	'use strict';

	angular.module('mrblack.auth').controller('ConfirmController', ['$scope', '$state', 'User', 'Toastr', '$window', function($scope, $state, User, Toastr, $window) {
		$scope.user = User.bindUser();
		$scope.smsCode = '';

		$scope.sendCode = function(phoneNumber) {
			var urlPhone = phoneNumber.replace(/ /g, '');

			User.getCode({
				phoneNumber: urlPhone
			}).then(function(response){
				Toastr.success(response.response, "Success");
			},function(response){
				Toastr.error(response.data.error, "Error");
			});
		};

		$scope.changePhone = function() {
			$scope.user.phoneNumber = '';
			$state.go('signin');
		};
	}]);
}());
