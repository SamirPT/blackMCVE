/**
 * Created by EGusev on 03.06.2016.
 */
(function () {
	angular.module('mrblack.main').controller('CustomersCtrl', ['$scope', 'VenueService', '$state', 'Toastr', '$modal', function ($scope, VenueService, $state, Toastr, $modal) {

		var selectedTags = [];
		$scope.tagsList = [];
		$scope.customers = [];
		$scope.currentPageIndex = 0;
		$scope.pageSize = 30;
		$scope.hasMoreCustomers = true;
		$scope.xlsSpinner = true;

		VenueService.getTags({id: $state.params.id}).then(
			function (response) {
				angular.forEach(response, function (tag) {
					$scope.tagsList.push({name: tag, selected: false});
				});
			},
			function (error) {
				Toastr.error(error.data.error, 'Error');
			}
		);

		$scope.selectTag = function (tag) {
			if (tag.selected) {
				tag.selected = false;
				var index = selectedTags.indexOf(tag.name);
				if (index !== -1) {
					selectedTags.splice(index, 1);
				}
			} else {
				tag.selected = true;
				selectedTags.push(tag.name);
			}
		};

		$scope.getCustomers = function (isFirstPack) {
			if (isFirstPack) {
				$scope.customers = [];
				$scope.hasMoreCustomers = true;
				$scope.currentPageIndex = 0;
			} else {
				$scope.currentPageIndex++;
			}
			var tags = selectedTags.join(',');

			VenueService.getCustomers({
				id: $state.params.id,
				tags: tags,
				pageIndex: $scope.currentPageIndex,
				pageSize: $scope.pageSize
			}).then(
				function (response) {
					$scope.customers = $scope.customers.concat(response);
					if (response.length < 30) {
						$scope.hasMoreCustomers = false;
					}
				},
				function (error) {
					Toastr.error(error.data.error, 'Error');
				}
			);
		};

		$scope.detailedCustomer = function (customer) {
			var modalInstance = $modal.open({
				templateUrl: '/assets/main/modules/menu/customers/detailed/detailed.html',
				controller: 'DetailedCustomersCtrl',
				resolve: {
					customer: function() {
						return customer || {};
					}
				}
			});

			modalInstance.result.then(detailedCustomerCallBack, detailedCustomerCallBack);
		};

		var detailedCustomerCallBack = function () {
			$scope.getCustomers();
		};
	}]);
}());