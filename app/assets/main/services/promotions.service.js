(function () {
	'use strict';

	angular.module('mrblack.main').service('PromotionsService', ['$resource', function($resource) {
		var Api = $resource('/api/v1/promotions/fb/:action', {}, {
			getFbUsers: {
				method: 'GET',
				isArray: true,
				params: {
					action: 'users'
				}
			}
		});

		var createFeedback = function (params, data) {
			return Api.save(params, data).$promise;
		};
		var getFbUsers = function(params, data) {
			return Api.getFbUsers(params, data).$promise;
		};

		return {
			createFeedback: createFeedback,
			getFbUsers: getFbUsers
		};
	}]);
}());