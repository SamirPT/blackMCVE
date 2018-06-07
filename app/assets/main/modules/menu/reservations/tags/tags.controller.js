/**
 * Created by EGusev on 27.05.2016.
 */
(function () {
	'use strict';

	angular.module('mrblack.main').controller('AddTagsCtrl', ['$scope', '$state', '$modalInstance', 'Toastr', 'VenueService','ReservationService', '$modal', 'reservation', function ($scope, $state, $modalInstance, Toastr, VenueService, ReservationService, $modal, reservation) {
		$scope.tags = [];

		var getVenueTags = function () {
			VenueService.getTags({id: $state.params.id}).then(
				function (response) {
					angular.forEach(response, function (tag) {
						if (reservation.tags.indexOf(tag) === -1) {
							$scope.tags.push({name: tag, selected: false});
						} else {
							$scope.tags.push({name: tag, selected: true});
						}
					});
				},
				function (error) {
					Toastr.error(error.data.error, 'Error');
				}
			);
		};
		getVenueTags();

		$scope.selectTag = function (tag) {
			tag.selected = !tag.selected;
		};

		$scope.saveTags = function () {
			var sendTags = [];
			angular.forEach($scope.tags, function (tag) {
				if (tag.selected) {
					sendTags.push(tag.name);
				}
			});
			reservation.tags = sendTags;
			ReservationService.tags({
				id: reservation.id
			},sendTags).then(function (response) {
				$scope.cancel();
			}, function (error) {
				Toastr.error(error.data.error, "Error");
			});
		};

		$scope.cancel = function () {
			$modalInstance.dismiss('cancel');
		};
	}]);
}());
