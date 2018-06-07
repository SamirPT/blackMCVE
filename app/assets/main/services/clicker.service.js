(function () {
	'use strict';

	angular.module('mrblack.main').service('ClickerService', ['$resource', function($resource) {
		var Api = $resource('/api/v1/clicker/:action',{},{
			sendAlert: {
				method: 'POST',
				params: {
					action: 'alert'
				}
			},
			getStats: {
				method: 'GET',
				params: {
					action: 'state'
				}
			},
			updateStats: {
				method: 'PUT',
				params: {
					action: 'state'
				}
			}
		});
		var clicked = {
			men: 0,
			women: 0
		};

		var sendAlert = function(params, data) {
			return Api.sendAlert(params, data).$promise;
		};
		var getStats = function(params, data) {
			return Api.getStats(params, data).$promise;
		};
		var updateStats = function(params, data) {
			return Api.updateStats(params, data).$promise;
		};

		var getClicked = function() {
			return clicked;
		};

		return {
			sendAlert: sendAlert,
			getStats: getStats,
			updateStats: updateStats,
			getClicked: getClicked
		};
	}]);
}());