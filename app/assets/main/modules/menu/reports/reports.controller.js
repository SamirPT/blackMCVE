(function () {
	'use strict';

	angular.module('mrblack.main').controller('ReportsCtrl', ['$scope', '$state', 'VenueService', 'Toastr', '$rootScope', function ($scope, $state, VenueService, Toastr, $rootScope) {
		$scope.venueId = $state.params.id;
	}]);
}());
