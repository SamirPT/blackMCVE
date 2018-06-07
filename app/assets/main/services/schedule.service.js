(function () {
	'use strict';

	angular.module('mrblack.main').service('ScheduleService', ['$resource', function($resource) {
		var Api = $resource('/api/v1/schedule');

		var getSchedule = function(params, data) {
			return Api.get(params, data).$promise;
		};

		return {
			getSchedule: getSchedule
		};
	}]);
}());