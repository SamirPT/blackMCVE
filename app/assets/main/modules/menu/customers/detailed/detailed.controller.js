(function () {
	angular.module('mrblack.main').controller('DetailedCustomersCtrl', ['$scope', 'VenueService','UserProfileService', 'DictService', '$state', 'Toastr', '$modalInstance', 'customer', function ($scope, VenueService, UserProfileService, DictService, $state, Toastr, $modalInstance, customer) {
		$scope.customer = customer;
		$scope.customerInfo = {};
		$scope.loading = true;
		$scope.hasChanged = false;

		$scope.bottleService = {
			TABLE: 'Table',
			STANDUP: 'Standup',
			BAR: 'Bar'
		};
		$scope.groupType = {
			MIX: 'Mix',
			ALL_GIRLS: 'All Girls',
			UNKNOWN: 'Unknown',
			PROMO: 'Promo',
			ALL_GUYS: 'All Guys',
			OWNER: 'Owner'
		};
		$scope.resTimes = [];
		$scope.reservationStatuses = DictService.getReservationStatuses();

		$scope.splitTimeAgo = function (reservationDate, time, index) {
			var parsedDate = reservationDate.split('/');
			var date = new Date(parsedDate[2] + '-' + parsedDate[1] + '-' + parsedDate[0] + 'T' + time);
			$scope.resTimes[index] = new Date(date.getTime() + date.getTimezoneOffset()*60000);
		};

		$scope.statusColor = function (status) {
			if (status === 'APPROVED' || status === 'CONFIRMED_COMPLETE' || status === 'COMPLETED') {
				return 'text-success-dk';
			}
		};

		DictService.getRoles().then(function(response){
			$scope.roles = response;
			$scope.promoRoles = {
				PROMOTER: 'Promoter',
				MANAGER: 'Manager'
			};
		}, function(response){
			Toastr.error(response.data.error, "Error");
		});

		var getCustomerInfo = function () {
			UserProfileService.getCustomerInfo({
				id: $scope.customer.id,
				venueId: $state.params.id
			}).then(function (response) {
				$scope.customerInfo = response;
				angular.forEach($scope.customerInfo.notes, function (note, key) {
					$scope.splitTimeAgo(note.shortReservationInfo.reservationDate, note.time, key);
				});
				$scope.loading = false;
			}, function (error) {
				Toastr.error(error.data.error, 'Error');
				$scope.loading = false;
			});
		};
		getCustomerInfo();

		$scope.saveChanges = function (firstTab) {
			$scope.loading = true;
			var data = {
				visitorInfo: {
					id: $scope.customerInfo.visitorInfo.id
				}
			};

			if (firstTab) {
				data.tags = $scope.customerInfo.tags;
				data.customerNote = $scope.customerInfo.customerNote;
			} else {
				data.contactInfo = {
					id: $scope.customerInfo.contactInfo.id ? $scope.customerInfo.contactInfo.id : null,
					firstName: $scope.customerInfo.contactInfo.firstName,
					lastName: $scope.customerInfo.contactInfo.lastName,
					address: $scope.customerInfo.contactInfo.address,
					city: $scope.customerInfo.contactInfo.city,
					state: $scope.customerInfo.contactInfo.state,
					country: $scope.customerInfo.contactInfo.country,
					postalCode: $scope.customerInfo.contactInfo.postalCode,
					email: $scope.customerInfo.contactInfo.email,
					companyName: $scope.customerInfo.contactInfo.companyName,
					phoneNumber: $scope.customerInfo.contactInfo.phoneNumber,
					dateOfBirth: $scope.customerInfo.contactInfo.dateOfBirth
				};
			}

			UserProfileService.updateCustomerInfo({
				venueId: $state.params.id
			}, data).then(function (response) {
				$scope.loading = false;
			}, function (error) {
				Toastr.error(error.data.error, 'Error');
				$scope.loading = false;
			});
		};

		$scope.cancel = function () {
			$modalInstance.dismiss('cancel');
		};

		$scope.checkOneCR = function (table, compliment) {
			if (compliment) {
				return table.complimentAll || table.complimentGirls || table.complimentGirlsQty || table.complimentGuys || table.complimentGuysQty;
			} else {
				return table.reducedAll || table.reducedGirls || table.reducedGirlsQty || table.reducedGuys || table.reducedGuysQty;
			}
		};
	}]);
}());