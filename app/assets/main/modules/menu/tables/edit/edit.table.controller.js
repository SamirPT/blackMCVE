(function () {
	'use strict';

	angular.module('mrblack.main').controller('EditTableCtrl', ['$scope', '$modalInstance', '$state', 'tables', 'date', 'EventsService', 'TablesService', 'Toastr', 'UtilService', function ($scope, $modalInstance, $state, tables, date, EventsService, TablesService, Toastr, UtilService) {
		$scope.tables = tables;
		$scope.date = date;
		$scope.currentTable = {};
		$scope.currentEvent = EventsService.getCurrentEvent();
		$scope.spinner = true;

		$scope.bottleService = {
			TABLE: 'Table',
			STANDUP: 'Standup',
			BAR: 'Bar'
		};

		$scope.cancel = function () {
			$scope.loading = false;
			$modalInstance.dismiss('cancel');
		};

		$scope.deleteTable = function(id, $event) {
			$event.stopPropagation();
			$scope.spinner = false;

			TablesService.deleteTable({
				id: id
			}).then(function (response) {
				Toastr.success("Table deleted", "Success");
				$scope.getSeating($scope.date);
				$scope.spinner = true;
			}, function (response) {
				Toastr.error(response.data.error, "Error");
				$scope.spinner = true;
			});
		};

		$scope.setCurrent = function(table) {
			$scope.currentTable = table;
		};

		$scope.createTable = function(table) {
			$scope.spinner = false;

			TablesService.createTable({},{
				placeNumber: parseInt(table.placeNumber),
				bottleServiceType: table.bottleServiceType,
				venueId: parseInt($state.params.id)
			}).then(function (response) {
				Toastr.success("Table created", "Success");
				$scope.getSeating($scope.date);
				$scope.spinner = true;
			}, function (response) {
				Toastr.error(response.data.error, "Error");
				$scope.spinner = true;
			});
		};

		$scope.updateTable = function (table) {
			$scope.spinner = false;

			TablesService.updateTable({},{
				id: parseInt(table.id),
				placeNumber: parseInt(table.placeNumber),
				bottleServiceType: table.bottleServiceType
			}).then(function (response) {
				Toastr.success("Table updated", "Success");
				$scope.getSeating($scope.date);
				$scope.spinner = true;
			}, function (response) {
				Toastr.error(response.data.error, "Error");
				$scope.spinner = true;
			});
		};

		var splitByAssignments = function (value, type) {
			if (!!value.reservationInfo) {
				$scope.tables[type].assigned.push(value);
			} else if (value.tableInfo.closed) {
				$scope.tables[type].closed.push(value);
			} else {
				$scope.tables[type].opened.push(value);
			}
		};

		var splitTables = function(response){
			$scope.tables = {
				tables: {
					opened: [],
					assigned: [],
					closed: []
				},
				standups:  {
					opened: [],
					assigned: [],
					closed: []
				},
				bars:  {
					opened: [],
					assigned: [],
					closed: []
				}
			};
			angular.forEach(response, function(value, key){
				if(value.tableInfo.bottleServiceType == 'TABLE') {
					splitByAssignments(value, 'tables');
				} else if(value.tableInfo.bottleServiceType == 'STANDUP') {
					splitByAssignments(value, 'standups');
				} else {
					splitByAssignments(value, 'bars');
				}
			});
		};

		$scope.getSeating = function(date) {
			$scope.spinner = false;
			TablesService.getSeating({
				venueId: $state.params.id,
				date: UtilService.formatRequestDate(date),
				eventId: $scope.currentEvent.id
			}).then(function (response) {
				$scope.tables = response;
				splitTables(response);
				$scope.spinner = true;
			}, function (response) {
				if(response.data) {
					Toastr.error(response.data.error, "Error");
					$scope.spinner = true;
				}
			});
		};
	}]);
}());
