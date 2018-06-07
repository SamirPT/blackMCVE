(function () {
	'use strict';

	angular.module('mrblack.main').controller('ClickerCtrl', ['$scope', '$state', '$interval', 'VenueService', 'ClickerService', 'Toastr', function ($scope, $state, $interval, VenueService, ClickerService, Toastr) {
		$scope.clicked = ClickerService.getClicked();

		$scope.alertType = {
			POLICE: "Police",
			FIRE: "Fire",
			LIQUOR_BOARD: "Liquor board",
			MLS: "MLS",
			CAPACITY_REACHED: "Capacity reached"
		};

		$scope.clickerTypes = {
			types: ['Only In', 'Only Out', 'Both'],
			selected: 'Both'
		};

		$scope.addMember = function (man) {
			if(man) {
				$scope.clicked.men++;
			} else {
				$scope.clicked.women++;
			}
		};

		$scope.delMember = function (man) {
			if(man) {
				$scope.clicked.men--;
			} else {
				$scope.clicked.women--;
			}
		};

		$scope.getStats = function() {
			ClickerService.getStats({
				venueId: $state.params.id
			}).then(function (response) {
				$scope.stats = response;
				$scope.percentWomen = parseInt(100 * $scope.stats.women/($scope.stats.women + $scope.stats.men));
				$scope.percentMen = parseInt(100 * $scope.stats.men/($scope.stats.men + $scope.stats.women));
				$scope.percentCapacity = parseInt(100 * ($scope.stats.totalIn - $scope.stats.totalOut)/$scope.stats.capacity);
			}, function (response) {
				Toastr.error(response.data.error, "Error");
			});
		};

		$scope.updateStats = function (men, women) {
			ClickerService.updateStats({
				venueId: $state.params.id
			},{
				men: men,
				women: women
			}).then(function (response) {
				$scope.clicked.men = 0;
				$scope.clicked.women = 0;
				$scope.getStats();
			}, function (response) {
				Toastr.error(response.data.error, "Error");
			});
		};

		$scope.getStats();
		if($state.current.name == 'clicker') {
			$scope.update = $interval(function () {
				$scope.getStats();
			}, 15000);
		}

		$scope.callMenu = function () {
			$state.go('clicker_menu',{id: $state.params.id});
		};

		$scope.back = function () {
			$state.go('clicker',{id: $state.params.id});
		};

		$scope.selectAlert = function (key) {
			$scope.selectedAlert = key;
		};

		$scope.$on('$destroy', function () {
			if(angular.isDefined($scope.update)){
				$interval.cancel($scope.update);
			}
		});

		$scope.sendAlert = function (alert) {
			ClickerService.sendAlert({},{
				venueId: $state.params.id,
				type: alert
			}).then(function (response) {
				Toastr.success("", "Success");
			}, function (response) {
				Toastr.error(response.data.error, "Error");
			});
		};
	}]);
}());
