(function () {
	'use strict';

	angular.module('mrblack.main').service('UtilService', [function() {
		var formatRequestDate = function (date) {
			return new Date(date.getTime() - (date.getTimezoneOffset() * 60000)).toISOString().slice(0,10);
		};

		return {
			formatRequestDate: formatRequestDate
		};
	}]);
}());
