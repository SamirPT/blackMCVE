(function () {
	'use strict';

	angular.module('mrblack.main').service('VenueService', ['$resource', function($resource) {
		var Api = {
			venue: $resource('/api/v1/venue/:id/:action', {}, {
				updateVenue: {
					method: 'PUT'
				},
				getNextEventDate: {
					method: 'GET',
					params: {
						action: 'nextdate'
					}
				},
				getReqEmpl: {
					method: 'GET',
					params: {
						action: 'people'
					}
				},
				getPrevEventDate: {
					method: 'GET',
					params: {
						action: 'prevdate'
					}
				},
				createRequest: {
					method: 'POST',
					params: {
						action: 'request'
					}
				},
				discardRequest: {
					method: 'DELETE',
					params: {
						action: 'request'
					}
				},
				updateState: {
					method: 'PUT',
					params: {
						action: 'request'
					}
				},
				getRequests: {
					method: 'GET',
					params: {
						action: 'requests'
					}
				},
				getSeatingList: {
					method: 'GET',
					params: {
						action: 'seating'
					}
				},
				getStaff: {
					method: 'GET',
					params: {
						action: 'staff'
					}
				},
				getStaffLists: {
					method: 'GET',
					params: {
						action: 'staffLists'
					}
				},
				addTag: {
					method: 'POST',
					params: {
						action:'tag'
					}
				},
				getTags: {
					method: 'GET',
					isArray: true,
					params: {
						action: 'tag'
					}
				},
				deleteTag: {
					method: 'DELETE',
					params: {
						action: 'tag'
					}
				},
				customers: {
					method: 'GET',
					isArray: true,
					params: {
						action: 'customers'
					}
				},
				snapshot: {
					method: 'GET',
					params: {
						action: 'snapshot'
					}
				},
				state: {
					method: 'PUT',
					params: {
						action: 'state'
					}
				},
				createPromoRequest: {
					method: 'POST',
					params: {
						action: 'promoter'
					}
				},
				deletePromoRequest: {
					method: 'DELETE',
					params: {
						action: 'promoter'
					}
				},
				updatePromoRequest: {
					method: 'PUT',
					params: {
						action: 'promoter'
					}
				},
				eod: {
					method: 'GET',
					params: {
						action: 'eod'
					}
				}
			}),
			venues: $resource('/api/v1/venues/:action', {}, {
				getMyVenues: {
					method: 'GET',
					isArray: true,
					params: {
						action: 'my'
					}
				}
			}),
			promoter: $resource('/api/v1/promoter/:id/:action/:my', {}, {
				getListVenues: {
					method: 'GET',
					isArray: true,
					params: {
						action: 'venues'
					}
				},
				getAssociatedVenues: {
					method: 'GET',
					isArray: true,
					params: {
						action: 'venues',
						my: 'my'
					}
				}
			})
		};

		var MANAGER = ['MANAGER'],
			ADVANCED = ['ASSISTANT_MANAGER', 'VIP_HOST'],
			SERVER = ['SERVER', 'BARTENDER'],
			BASIC = ['PROMOTER', 'WILL_CALL', 'INVENTORY', 'BUSSER', 'HEAD_BUSSER', 'GUEST_LIST', 'SECURITY',
				'FRONT_DOOR_SECURITY', 'BARBACK', 'COAT_CHECK', 'HEAD_DOORMAN',
				'FRONT_DOOR_CASH'];

		var canViewAndModifyAllReservations = function(roles) {
			var perm = false;
			angular.forEach(roles, function (role) {
				if (MANAGER.includes(role) || ADVANCED.includes(role)) perm = true;
			});
			return perm;
		};

		var canViewAndModifyAllGuestListResos = function(roles) {
			return roles.includes('GUEST_LIST');
		};

		var canOnlyViewAllResos = function(roles) {
			return roles.includes('HEAD_DOORMAN');
		};

		var canWorkWithClicker = function(roles) {
			return roles.includes('HEAD_DOORMAN') || roles.includes('SECURITY') ||
				roles.includes('FRONT_DOOR_SECURITY');
		};

		var canViewAndManageTableView = function(roles) {
			var perm = false;
			angular.forEach(roles, function (role) {
				if (MANAGER.includes(role) || ADVANCED.includes(role)) perm = true;
			});
			return perm;
		};

		var canReleaseAndCompleteAllTables = function(roles) {
			var perm = false;
			angular.forEach(roles, function (role) {
				if (MANAGER.includes(role) || ADVANCED.includes(role)) perm = true;
			});
			return perm;
		};

		var canReleaseAndCompleteTablesAssignedToMe = function(roles) {
			var perm = false;
			angular.forEach(roles, function (role) {
				if (SERVER.includes(role) || MANAGER.includes(role) || ADVANCED.includes(role)) perm = true;
			});
			return perm;
		};

		var canViewClientsSection = function(roles) {
			var perm = false;
			angular.forEach(roles, function (role) {
				if (MANAGER.includes(role) || ADVANCED.includes(role)) perm = true;
			});
			return perm;
		};

		var canSeeAndUseReportsSection = function(roles) {
			var perm = false;
			angular.forEach(roles, function (role) {
				if (MANAGER.includes(role) || ADVANCED.includes(role)) perm = true;
			});
			return perm;
		};

		var canControlBsMinsAndCloseBsGl = function(roles) {
			var perm = false;
			angular.forEach(roles, function (role) {
				if (MANAGER.includes(role) || ADVANCED.includes(role)) perm = true;
			});
			return perm;
		};

		var canManageVenueInfo = function(roles) {
			var perm = false;
			angular.forEach(roles, function (role) {
				if (MANAGER.includes(role)) perm = true;
			});
			return perm;
		};

		var canCreateAndModifyEventInfo = function(roles) {
			var perm = false;
			angular.forEach(roles, function (role) {
				if (MANAGER.includes(role) || ADVANCED.includes(role)) perm = true;
			});
			return perm;
		};

		var canViewAndManageEmployeeSection = function(roles) {
			var perm = false;
			angular.forEach(roles, function (role) {
				if (MANAGER.includes(role)) perm = true;
			});
			return perm;
		};

		var canArriveReservationsAndUseClicker = function(roles) {
			var perm = false;
			angular.forEach(roles, function (role) {
				if (MANAGER.includes(role) || ADVANCED.includes(role)) perm = true;
			});
			return perm;
		};

		var canViewAndMakeNotesOnAllReservations = function(roles) {
			var perm = false;
			angular.forEach(roles, function (role) {
				if (MANAGER.includes(role) || ADVANCED.includes(role)) perm = true;
			});
			return perm;
		};

		var canCreateAndModifyTags = function(roles) {
			var perm = false;
			angular.forEach(roles, function (role) {
				if (MANAGER.includes(role) || ADVANCED.includes(role)) perm = true;
			});
			return perm;
		};

		var canAssignTags = function(roles) {
			var perm = false;
			angular.forEach(roles, function (role) {
				if (MANAGER.includes(role) || ADVANCED.includes(role) || SERVER.includes(role)) perm = true;
			});
			return perm;
		};

		var canViewFullSnapshotInfo = function(roles) {
			var perm = false;
			angular.forEach(roles, function (role) {
				if (MANAGER.includes(role) || ADVANCED.includes(role)) perm = true;
			});
			return perm;
		};

		var canViewEODStatementAndConfirm = function(roles) {
			return (roles && roles.includes('MANAGER'));
		};

		var getVenues = function() {
			return Api.venues.query().$promise;
		};
		var getMyVenues = function() {
			return Api.venues.getMyVenues().$promise;
		};
		var createVenue = function(params, data) {
			return Api.venue.save(params, data).$promise;
		};
		var updateVenue = function(params, data) {
			return Api.venue.updateVenue(params, data).$promise;
		};
		var deleteVenue = function(params, data) {
			return Api.venue.delete(params, data).$promise;
		};
		var getVenue = function(params, data) {
			return Api.venue.get(params, data).$promise;
		};
		var getNextEventDate = function(params, data) {
			return Api.venue.getNextEventDate(params, data).$promise;
		};
		var getReqEmpl = function(params, data) {
			return Api.venue.getReqEmpl(params, data).$promise;
		};
		var getPrevEventDate = function(params, data) {
			return Api.venue.getPrevEventDate(params, data).$promise;
		};
		var createRequest = function(params, data) {
			return Api.venue.createRequest(params, data).$promise;
		};
		var discardRequest = function(params, data) {
			return Api.venue.discardRequest(params, data).$promise;
		};
		var updateState = function(params, data) {
			return Api.venue.updateState(params, data).$promise;
		};
		var getRequests = function(params, data) {
			return Api.venue.getRequests(params, data).$promise;
		};
		var getSeatingList = function(params, data) {
			return Api.venue.getSeatingList(params, data).$promise;
		};
		var getStaff = function(params, data) {
			return Api.venue.getStaff(params, data).$promise;
		};
		var addTag = function (params, data) {
			return Api.venue.addTag(params, data).$promise;
		};
		var getTags = function (params, data) {
			return Api.venue.getTags(params, data).$promise;
		};
		var deleteTag =function (params,data) {
			return Api.venue.deleteTag(params, data).$promise;	
		};
		var getStaffLists = function(params, data) {
			return Api.venue.getStaffLists(params, data).$promise;
		};
		var getCustomers = function (params, data) {
			return Api.venue.customers(params, data).$promise;
		};
		var venueSnapshot = function (params, data) {
			return Api.venue.snapshot(params, data).$promise;
		};
		var venueState = function (params, data) {
			return Api.venue.state(params, data).$promise;
		};
		var createPromoRequest = function(params, data) {
			return Api.venue.createPromoRequest(params, data).$promise;
		};
		var deletePromoRequest = function(params, data) {
			return Api.venue.deletePromoRequest(params, data).$promise;
		};
		var updatePromoRequest = function(params, data) {
			return Api.venue.updatePromoRequest(params, data).$promise;
		};
		var getListVenues = function (params, data) {
			return Api.promoter.getListVenues(params, data).$promise;
		};
		var getAssociatedVenues = function (params, data) {
			return Api.promoter.getAssociatedVenues(params, data).$promise;
		};
		var getEODStatement = function (params, data) {
			return Api.venue.eod(params, data).$promise;
		};

		var venueId = '';
		var utilGetVenueId = function () {
			return venueId;	
		};
		var utilSetVenueId = function (id) {
			venueId = id;	
		};

		var venueRoles = {};
		var setPermissions = function (roles) {
			venueRoles.canViewAndModifyAllReservations = canViewAndModifyAllReservations(roles);
			venueRoles.canViewAndModifyAllGuestListResos = canViewAndModifyAllGuestListResos(roles);
			venueRoles.canOnlyViewAllResos = canOnlyViewAllResos(roles);
			venueRoles.canWorkWithClicker = canWorkWithClicker(roles);
			venueRoles.canViewAndManageTableView = canViewAndManageTableView(roles);
			venueRoles.canReleaseAndCompleteAllTables = canReleaseAndCompleteAllTables(roles);
			venueRoles.canReleaseAndCompleteTablesAssignedToMe = canReleaseAndCompleteTablesAssignedToMe(roles);
			venueRoles.canViewClientsSection = canViewClientsSection(roles);
			venueRoles.canSeeAndUseReportsSection = canSeeAndUseReportsSection(roles);
			venueRoles.canControlBsMinsAndCloseBsGl = canControlBsMinsAndCloseBsGl(roles);
			venueRoles.canManageVenueInfo = canManageVenueInfo(roles);
			venueRoles.canCreateAndModifyEventInfo = canCreateAndModifyEventInfo(roles);
			venueRoles.canViewAndManageEmployeeSection = canViewAndManageEmployeeSection(roles);
			venueRoles.canArriveReservationsAndUseClicker = canArriveReservationsAndUseClicker(roles);
			venueRoles.canViewAndMakeNotesOnAllReservations = canViewAndMakeNotesOnAllReservations(roles);
			venueRoles.canCreateAndModifyTags = canCreateAndModifyTags(roles);
			venueRoles.canAssignTags = canAssignTags(roles);
			venueRoles.canViewFullSnapshotInfo = canViewFullSnapshotInfo(roles);
			venueRoles.canViewEODStatementAndConfirm = canViewEODStatementAndConfirm(roles);
		};

		var getPermissions = function () {
			return venueRoles;
		};

		var isPromo = true;
		var setIsPromo = function (is) {
			isPromo = is;
		};
		var getIsPromo = function () {
			return isPromo;
		};

		return {
			getVenues: getVenues,
			getMyVenues: getMyVenues,
			createVenue: createVenue,
			updateVenue: updateVenue,
			deleteVenue: deleteVenue,
			getVenue: getVenue,
			getNextEventDate: getNextEventDate,
			getReqEmpl: getReqEmpl,
			getPrevEventDate: getPrevEventDate,
			createRequest: createRequest,
			discardRequest: discardRequest,
			updateState: updateState,
			getRequests: getRequests,
			getSeatingList: getSeatingList,
			getStaff: getStaff,
			addTag: addTag,
			getTags: getTags,
			deleteTag: deleteTag,
			getStaffLists: getStaffLists,
			getCustomers: getCustomers,
			venueSnapshot: venueSnapshot,
			venueState: venueState,
			utilGetVenueId: utilGetVenueId,
			utilSetVenueId: utilSetVenueId,
			setPermissions: setPermissions,
			getPermissions: getPermissions,
			setIsPromo: setIsPromo,
			getIsPromo: getIsPromo,
			createPromoRequest: createPromoRequest,
			deletePromoRequest: deletePromoRequest,
			updatePromoRequest: updatePromoRequest,
			getListVenues: getListVenues,
			getAssociatedVenues: getAssociatedVenues,
			getEODStatement: getEODStatement
		};
	}]);
}());