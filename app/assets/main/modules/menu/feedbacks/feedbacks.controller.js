(function () {
	'use strict';

	angular.module('mrblack.main').controller('FeedbacksCtrl', ['$scope', '$state', 'FeedbacksService', 'DictService', 'Toastr', 'VenueService', '$modal', function ($scope, $state, FeedbacksService, DictService, Toastr, VenueService, $modal) {
		$scope.guest = $state.params.guestInfo;
		$scope.state = $state;
		$scope.spinner = false;

		if(!$state.params.reservationId) {
			$state.go('venue_snapshot', {id: $state.params.id});
		}

		DictService.getRoles().then(function(response){
			$scope.roles = response;
		}, function(response){
			Toastr.error(response.data.error, "Error");
		});

		var feedbacksResponse = function(response) {
			$scope.spinner = false;
			if(response.$resolved) {
				$scope.feedbacks = response.feedbackList;
			} else {
				Toastr.error(response.data.error, "Error");
			}
		};

		$scope.getFeedbacks = function () {
			$scope.spinner = true;
			if($state.params.userId) {
				FeedbacksService.getFeedbacks({
					userId: $state.params.userId
				}).then(feedbacksResponse, feedbacksResponse);
			} else {
				FeedbacksService.getFeedbacks({
					reservationId: $state.params.reservationId
				}).then(feedbacksResponse, feedbacksResponse);
			}
		};
		$scope.getFeedbacks();

		var editFeedbackCallback = function () {
			$scope.getFeedbacks();
		};

		$scope.editFeedback = function (feedback) {
			var modalInstance = $modal.open({
				templateUrl: '/assets/main/modules/menu/feedbacks/edit/edit.feedback.html',
				controller: 'EditFeedbackCtrl',
				resolve: {
					feedback: function (){
						return feedback;
					}
				}
			});

			modalInstance.result.then(editFeedbackCallback, editFeedbackCallback);
		};
	}]);
}());
