(function () {
	'use strict';

	angular.module('mrblack.main').service('FeedbacksService', ['$resource', function($resource) {
		var Api = $resource('/api/v1/feedbacks/:id',{},{
			updateFeedback: {
				method: 'PUT'
			}
		});

		var getFeedbacks = function(params, data) {
			return Api.get(params, data).$promise;
		};
		var createFeedback = function(params, data) {
			return Api.save(params, data).$promise;
		};
		var updateFeedback = function(params, data) {
			return Api.updateFeedback(params, data).$promise;
		};
		var deleteFeedback = function(params, data) {
			return Api.delete(params, data).$promise;
		};

		return {
			getFeedbacks: getFeedbacks,
			createFeedback: createFeedback,
			updateFeedback: updateFeedback,
			deleteFeedback: deleteFeedback
		};
	}]);
}());