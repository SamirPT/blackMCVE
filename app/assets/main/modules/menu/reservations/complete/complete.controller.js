/**
 * Created by EGusev on 27.05.2016.
 */
(function () {
	'use strict';

	angular.module('mrblack.main').controller('CompleteResCtrl', ['$scope', '$state', '$modalInstance', 'Toastr', 'ReservationService', 'UserProfileService', 'FeedbacksService', '$modal', 'permission', 'reservation', function ($scope, $state, $modalInstance, Toastr, ReservationService, UserProfileService, FeedbacksService, $modal, permission, reservation) {
		$scope.loading = false;
		$scope.existLoading = false;
		$scope.permission = permission;
		$scope.reservation = reservation;
		$scope.reservation.completionFeedback = {};

		var onLoad = function () {
			if (!$scope.reservation.payees) {
				$scope.reservation.payees = [];
				$scope.reservation.payees.push({
					// fullName: $scope.reservation.guestInfo.fullName,
					email: $scope.reservation.guestInfo.email,
					phoneNumber: $scope.reservation.guestInfo.phoneNumber,
					dateOfBirth: $scope.reservation.guestInfo.birthday
				});
			}
		};
		onLoad();

		$scope.useExistingPayInfo = function () {
			$scope.existLoading = true;
			UserProfileService.getCustomerInfo({
				id: $scope.reservation.guestInfo.id,
				venueId: $state.params.id
			}).then(function (response) {
				$scope.reservation.payees[0] = response.contactInfo;
				$scope.existLoading = false;
			}, function (error) {
				Toastr.error(error.data.error, 'Error');
				$scope.existLoading = false;
			});
		};

		$scope.addNewPayeeInfo = function () {
			$scope.reservation.payees.push({});
		};

		$scope.btnDisabled = function () {
			if (!$scope.permission) {
				return !$scope.reservation.completionFeedback.message || !$scope.reservation.completionFeedback.rating;
			} else {
				return false;
			}
		};

		$scope.addTags = function () {
			var modalInstance = $modal.open({
				templateUrl: '/assets/main/modules/menu/reservations/tags/tags.html',
				controller: 'AddTagsCtrl',
				windowClass: 'menu-tables',
				resolve: {
					reservation: function () {
						return $scope.reservation;
					}
				}
			});
		};

		$scope.addNewPay = function () {
			$scope.reservation.payees.push({});
		};

		$scope.removePay = function (index) {
			$scope.reservation.payees.splice(index, 1);
		};

		$scope.addNote = function () {
			FeedbacksService.createFeedback({},{
				shortReservationInfo: {
					reservationId: $scope.reservation.id
				},
				message: $scope.reservation.completionFeedback.message,
				rating: $scope.reservation.completionFeedback.rating,
				tags: []
			}).then(function (response) {
				$scope.cancel();
			}, function (response) {
				Toastr.error(response.data.error, "Error");
			});
		};

		$scope.completeReservation = function () {
			ReservationService.updateReservation($scope.reservation).then(
				function (response) {
					ReservationService.state({id: $scope.reservation.id, newState: 'COMPLETED'}).then(
						function (response) {
							$scope.cancel();
						},
						function (error) {
							Toastr.error(error.data.error, 'Error');
						}
					);
				},
				function (error) {
					Toastr.error(error.data.error, 'Error');
				}
			);
		};

		$scope.cancel = function () {
			$modalInstance.dismiss('cancel');
		};
	}]);
}());
