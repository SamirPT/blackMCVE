(function () {
	'use strict';

	angular.module('mrblack.main').service('DictService', ['$q', '$resource', function($q, $resource) {
		var deferred = $q.defer();
		var Api = $resource('/api/v1/dict/:action', {}, {
			getRoles: {
				method: 'GET',
				params: {
					action: 'roles'
				}
			}
		});

		var reservationStatuses = {
			null: 'Guestlist',
			PENDING: 'Pending',
			APPROVED: 'Approved',
			REJECTED: 'Rejected',
			ARRIVED: 'Arrived',
			PRE_RELEASED: 'Released',
			RELEASED: 'Released',
			COMPLETED: 'Completed',
			NO_SHOW: 'No Show',
			CONFIRMED_COMPLETE: 'Finalized'
		};

		var promoRoles = {
			MANAGER: 'Manager',
			PROMOTER: 'Promoter'
		};

		var loadRoles = function() {
			return Api.getRoles().$promise.then(function(response) {
				deferred.resolve(response);
			}, function(err) {
				console.log(err);
			});
		};
		var getRoles = function() {
			return deferred.promise;
		};
		var getPromoRoles = function() {
			return promoRoles;
		};
		var getReservationStatuses = function () {
			return reservationStatuses;
		};
		return {
			loadRoles: loadRoles,
			getRoles: getRoles,
			getPromoRoles: getPromoRoles,
			getReservationStatuses: getReservationStatuses
		};
	}]);
}());
