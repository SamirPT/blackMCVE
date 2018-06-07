(function () {
	'use strict';

	angular.module('mrblack.auth').controller('SignUpController', ['$scope', '$state', 'User', 'Toastr', function($scope, $state, User, Toastr) {
		$scope.datePattern = /^(0[1-9]|1[0-9]|2[0-9]|3[01])-?(0[1-9]|1[012])-?(\d+)/;
		$scope.emailPattern = /.+@.+\..+/;
		$scope.user = User.bindUser();
		$scope.spinner = true;

		$scope.signUp = function(phoneNumber){
			$scope.spinner = false;
			var urlPhone = phoneNumber.replace(/ /g, '');
			var date = $scope.user.birthday,
					dateParts = date.split("-"),
					birthday = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];

			User.createUser({
				fullName: $scope.user.fullName,
				email: $scope.user.email,
				phoneNumber: urlPhone,
				birthday: birthday
			}).then(function(response){
				$scope.spinner = true;
				$state.go('confirm');
			}, function(response){
				$scope.spinner = true;
				Toastr.error(response.data.error, "Error");
				if(response.data.fields){
					var fields = response.data.fields.split(',');
					angular.forEach(fields, function(value, key){
						$scope.signupForm[value].$setValidity(value, false);
					});
				}
			});
		};
	}]);
}());