(function () {
	'use strict';

	angular.module('mrblack.main').controller('EditFeedbackCtrl', ['$scope', '$modalInstance', 'Toastr', '$state', 'feedback', 'VenueService', 'FeedbacksService', function ($scope, $modalInstance, Toastr, $state, feedback, VenueService, FeedbacksService) {
		$scope.feedback = feedback;
		$scope.create = $scope.feedback ? true : false;
		$scope.tagsList = [];
		$scope.permissions = VenueService.getPermissions();

		$scope.cancel = function () {
			$scope.loading = false;
			$modalInstance.dismiss('cancel');
		};

		VenueService.getTags({id: $state.params.id}).then(
			function (response) {
				angular.forEach(response, function (tag) {
					var selected = !!$scope.feedback && $scope.feedback.tags.indexOf(tag) !== -1;
					$scope.tagsList.push({name: tag, selected: selected});
				});
			},
			function (error) {
				Toastr.error(error.data.error, 'Error');
			}
		);

		$scope.selectTag = function (tag) {
			tag.selected = !tag.selected;
		};

		var tagsProcessing = function (tagsArray) {
			var feedbackTags = [];
			angular.forEach(tagsArray, function (tag) {
				if (tag.selected) {
					feedbackTags.push(tag.name);
				}
			});

			return feedbackTags;
		};

		$scope.sendFeedback = function () {
			var tags = tagsProcessing($scope.tagsList);
			FeedbacksService.createFeedback({},{
				shortReservationInfo: {
					reservationId: $state.params.reservationId
				},
				message: $scope.feedback.message,
				rating: $scope.feedback.rating,
				tags: tags
			}).then(function (response) {
				$scope.cancel();
			}, function (response) {
				Toastr.error(response.data.error, "Error");
			});
		};

		$scope.updateFeedback = function () {
			var tags = tagsProcessing($scope.tagsList);
			FeedbacksService.updateFeedback({},{
				id: $scope.feedback.id,
				message: $scope.feedback.message,
				rating: $scope.feedback.rating,
				tags: tags
			}).then(function (response) {
				$scope.cancel();
			}, function (response) {
				Toastr.error(response.data.error, "Error");
			});
		};

		$scope.deleteFeedback = function () {
			FeedbacksService.deleteFeedback({
				id: $scope.feedback.id
			}).then(function (response) {
				$scope.cancel();
			}, function (response) {
				Toastr.error(response.data.error, "Error");
			});
		};
	}]);
}());
