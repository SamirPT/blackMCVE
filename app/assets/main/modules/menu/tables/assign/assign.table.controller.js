(function () {
	'use strict';

	angular.module('mrblack.main').controller('AssignTableCtrl', ['$scope', '$modalInstance', 'table', 'Toastr', 'ReservationService', '$state', 'date', 'EventsService', 'UtilService', function ($scope, $modalInstance, table, Toastr, ReservationService, $state, date, EventsService, UtilService) {
		$scope.loading = false;
		$scope.state = $state;
		$scope.table = table;
		$scope.reservationFilter = '';
		$scope.bottleService = {
			TABLE: 'Table',
			STANDUP: 'Standup',
			BAR: 'Bar'
		};
		$scope.currentEvent = EventsService.getCurrentEvent();
		$scope.groupType = {
			MIX: 'Mix',
			ALL_GIRLS: 'All Girls',
			UNKNOWN: 'Unknown',
			PROMO: 'Promo',
			ALL_GUYS: 'All Guys',
			OWNER: 'Owner'
		};

		$scope.cancel = function () {
			$scope.loading = false;
			$modalInstance.dismiss('cancel');
		};

		$scope.showFeedbacks = function (reservation) {
			$scope.cancel();
			$state.go('feedbacks', {id: $state.params.id, reservationId: reservation.id, guestInfo: reservation.guestInfo});
		};

		var getReservations = function () {
			ReservationService.getInfoList({
				venueId: $state.params.id,
				date: UtilService.formatRequestDate(date),
				bsType: $scope.table.tableInfo.bottleServiceType,
				eventId: $scope.currentEvent.id
			}).then(function (response) {
				$scope.reservations = response;
			}, function (response) {
				Toastr.error(response.data.error, "Error");
				$scope.cancel();
			});
		};
		getReservations();

		$scope.assignTable = function (reservation) {
			$scope.loading = true;
			ReservationService.assignTable({
				id: reservation.id
			},{
				id: $scope.table.tableInfo.id
			}).then(function (response) {
				$scope.cancel();
			}, function (response) {
				$scope.loading = false;
				Toastr.error(response.data.error, "Error");
			});
		};

		$scope.checkOneCR = function (reservation, compliment) {
			if (compliment) {
				return reservation.complimentAll || reservation.complimentGirls || reservation.complimentGirlsQty || reservation.complimentGuys || reservation.complimentGuysQty;
			} else {
				return reservation.reducedAll || reservation.reducedGirls || reservation.reducedGirlsQty || reservation.reducedGuys || reservation.reducedGuysQty;
			}
		};
	}]);
}());
