(function () {
	'use strict';

	angular.module('mrblack.main').controller('StaffTableCtrl', ['$scope', '$modalInstance', 'table', 'Toastr', 'ReservationService', '$state', 'date', 'VenueService', 'DictService', '$q', function ($scope, $modalInstance, table, Toastr, ReservationService, $state, date, VenueService, DictService, $q) {
		$scope.state = $state;
		$scope.table = table;
		$scope.bottleService = {
			TABLE: 'Table',
			STANDUP: 'Standup',
			BAR: 'Bar'
		};
		$scope.selectedStaff = {
			SERVER: {},
			BUSSER: {},
			VIP_HOST: {}
		};
		$scope.deselectedStaff = {
			SERVER: {},
			BUSSER: {},
			VIP_HOST: {}
		};
		$scope.countSelected = 0;
		$scope.countDeselected = 0;

		angular.forEach($scope.table.reservationInfo.staff, function (value) {
			$scope.selectedStaff[value.role][value.id] = 'first';
		});

		$scope.cancel = function () {
			$scope.loading = false;
			$modalInstance.dismiss('cancel');
		};

		var getRoles = function () {
			DictService.getRoles().then(function(response){
				$scope.roles = response;
			}, function(response){
				Toastr.error(response.data.error, "Error");
			});
		};
		getRoles();

		var getStaffLists = function() {
			VenueService.getStaffLists({
				id: $state.params.id
			}).then(function (response) {
				$scope.staff = response;
			}, function (response) {
				Toastr.error(response.data.error, "Error");
			});
		};
		getStaffLists();

		$scope.selectStaff = function (role, staffId) {
			if (!$scope.selectedStaff[role][staffId] && $scope.deselectedStaff[role][staffId] !== 'first') {
				$scope.selectedStaff[role][staffId] = true;
				$scope.countSelected++;
			} else if ($scope.selectedStaff[role][staffId] === 'first') {
				$scope.selectedStaff[role][staffId] = false;
				$scope.deselectedStaff[role][staffId] = 'first';
				$scope.countDeselected++;
			} else if ($scope.deselectedStaff[role][staffId] === 'first') {
				$scope.selectedStaff[role][staffId] = 'first';
				$scope.deselectedStaff[role][staffId] = false;
				$scope.countDeselected--;
			} else {
				$scope.selectedStaff[role][staffId] = false;
				$scope.countSelected--;
			}
		};

		var pushToArray = function (object, select) {
			var array = [];
			angular.forEach(object, function (value, role) {
				angular.forEach(value, function (staff, staffId) {
					if (staff === select) {
						array.push({
							id: staffId,
							role: role
						});
					}
				});
			});
			return array;
		};

		$scope.assignStaffBatch = function () {
			var arraySelectedStaff = pushToArray($scope.selectedStaff, true);
			var arrayDeselectedStaff = pushToArray($scope.deselectedStaff, 'first');

			$q.all([ReservationService.assignStaffBatch({id: table.reservationInfo.id}, arraySelectedStaff),
					ReservationService.unassignStaffBatch({id: table.reservationInfo.id}, arrayDeselectedStaff)])
			.then(function (response) {
				$scope.cancel();
			}, function (response) {
				Toastr.error(response.data.error, "Error");
			});
		};
	}]);
}());
