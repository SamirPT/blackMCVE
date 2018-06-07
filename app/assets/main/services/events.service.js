(function () {
	'use strict';

	angular.module('mrblack.main').service('EventsService', ['$resource', function($resource) {
		var Api = $resource('/api/v1/events/:id:action', {}, {
			updateEvent: {
				method: 'PUT'
			},
			getMonthEvents: {
				method: 'GET',
				params: {
					action: 'calendar'
				}
			}
		});

		var getEventsReq;

		var currentEvent = {};

		var getCurrentEvent = function () {
			return currentEvent;
		};

		var setCurrentEvent = function (event) {
			angular.forEach(event, function (value, key) {
				currentEvent[key] = value;
			});
		};

		var clearCurrentEvent = function () {
			currentEvent = {};
		};

		var getEvents = function(params, data) {
			if (angular.isObject(getEventsReq) && angular.isFunction(getEventsReq.$cancelRequest)) {
				getEventsReq.$cancelRequest();
			}
			getEventsReq = Api.get(params, data);
			return getEventsReq.$promise;
		};
		var createEvent = function(params, data) {
			return Api.save(params, data).$promise;
		};
		var updateEvent = function(params, data) {
			return Api.updateEvent(params, data).$promise;
		};
		var getMonthEvents = function(params, data) {
			return Api.getMonthEvents(params, data).$promise;
		};
		var deleteEvent = function(params, data) {
			return Api.delete(params, data).$promise;
		};

		return {
			getCurrentEvent: getCurrentEvent,
			setCurrentEvent: setCurrentEvent,
			clearCurrentEvent: clearCurrentEvent,
			getEvents: getEvents,
			createEvent: createEvent,
			updateEvent: updateEvent,
			getMonthEvents: getMonthEvents,
			deleteEvent: deleteEvent
		};
	}]);
}());