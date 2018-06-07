(function () {
	'use strict';

	angular.module('mrblack.auth')
		.factory('User', function($resource) {
			var Api = $resource('api/v1/user/:action', {}, {
				getCode: {
					method: 'GET',
					params: {
						action: 'code'
					}
				}
			});

			var user = {
				fullName: '',
				email: '',
				phoneNumber: '',
				birthday: ''
			};

			var createUser = function(params) {
				return Api.save(params).$promise;
			};
			var getCode = function(params) {
				return Api.getCode(params).$promise;
			};
			var bindUser = function() {
				return user;
			};

			return {
				createUser: createUser,
				getCode: getCode,
				bindUser: bindUser
			};
		});
}());