(function () {
	'use strict';

	angular.module('mrblack.main').service('TablesService', ['$resource', function($resource) {
		var Api = {
			action: $resource('/api/v1/tables/:id:action',{},{
				updateTable: {
					method: 'PUT'
				},
				getSeating: {
					method: 'GET',
					isArray: true,
					params: {
						action: 'seating'
					}
				},
				close: {
					method: 'PUT',
					params: {
						action: '/close'
					}
				},
				open: {
					method: 'PUT',
					params: {
						action: '/open'
					}
				}
			}),
			table: $resource('/api/v1/tables/:id/:action',{}, {
				close: {
					method: 'PUT',
					params: {
						action: 'close'
					}
				},
				open: {
					method: 'PUT',
					params: {
						action: 'open'
					}
				}
			})
		};

		var getTablesReq;

		var getTables = function(params, data) {
			if (angular.isObject(getTablesReq) && angular.isFunction(getTablesReq.$cancelRequest)) {
				getTablesReq.$cancelRequest();
			}
			getTablesReq = Api.action.get(params, data);
			return getTablesReq.$promise;
		};
		var createTable = function(params, data) {
			return Api.action.save(params, data).$promise;
		};
		var updateTable = function(params, data) {
			return Api.action.updateTable(params, data).$promise;
		};
		var getSeating = function(params, data) {
			return Api.action.getSeating(params, data).$promise;
		};
		var deleteTable = function(params, data) {
			return Api.action.delete(params, data).$promise;
		};
		var closeTable = function(params, data) {
			return Api.table.close(params, data).$promise;
		};
		var openTable = function(params, data) {
			return Api.table.open(params, data).$promise;
		};

		return {
			getTables: getTables,
			createTable: createTable,
			updateTable: updateTable,
			getSeating: getSeating,
			deleteTable: deleteTable,
			closeTable: closeTable,
			openTable: openTable
		};
	}]);
}());