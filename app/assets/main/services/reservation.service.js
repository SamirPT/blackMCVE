/**
 * Created by EGusev on 17.04.2016.
 */
(function () {
   'use strict';

    angular.module('mrblack.main').service('ReservationService', ['$resource', function ($resource) {

        var Api = {
            reservation: $resource('/api/v1/reservation/:id/:act',
                {}, {
                    create: {
                        method: 'POST'
                    },
                    update: {
                        method: 'PUT'
                    },
                    delete: {
                        method: 'DELETE'
                    },
                    get: {
                        method: 'GET'
                    },
                    approve: {
                        method: 'PUT',
                        params: {
                            act: 'approve'
                        }
                    },
                    assignStaffBatch: {
                        method: 'PUT',
                        params: {
                            act: 'assignStaffBatch'
                        }
                    },
                    assignTable: {
                        method: 'PUT',
                        params: {
                            act: 'assignTable'
                        }
                    },
                    guests: {
                        method: 'PUT',
                        params: {
                            act: 'guests'
                        }
                    },
                    unassignStaffBatch: {
                        method: 'PUT',
                        params: {
                            act: 'unassignStaffBatch'
                        }
                    },
                    move: {
                        method: 'PUT',
                        params: {
                            act: 'move'
                        }
                    },
                    arrive: {
                        method: 'PUT',
                        params: {
                            act: 'arrive'
                        }
                    },
                    reactivate: {
                        method: 'PUT',
                        params: {
                            act: 'reactivate'
                        }
                    },
                    state: {
                        method: 'PUT',
                        params: {
                            act: 'state'
                        }
                    },
                    tags: {
                        method: 'PUT',
                        params: {
                            act: 'tags'
                        }
                    },
                    unassign: {
                        method: 'PUT',
                        params: {
                            act: 'unassign'
                        }
                    }
                }
            ),
            reservations: $resource('/api/v1/reservations/:action',
                {}, {
                    getReservations: {
                        method: 'GET'
                    },
                    getInfoList: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            action: 'bs'
                        }
                    }
                }
            )
        };
        
        var getReservationsReq;

        var createReservation = function (reservation) {
            return Api.reservation.create(reservation).$promise;
        };

        var updateReservation = function (reservation) {
            return Api.reservation.update(reservation).$promise;
        };

        var deleteReservation = function (id) {
            return Api.reservation.delete({id: id}).$promise;
        };

        var getReservation = function (id) {
            return Api.reservation.get({id: id}).$promise;
        };

        var approveReservation = function (id) {
            return Api.reservation.approve({id: id}, {}).$promise;
        };

        var assignStaffBatch = function (id, staffInfo) {
            return Api.reservation.assignStaffBatch(id, staffInfo).$promise;
        };
        
        var assignTable = function (id, tableInfo) {
            return Api.reservation.assignTable(id, tableInfo).$promise;
        };
        
        var guests = function (params, data) {
            return Api.reservation.guests(params, data).$promise;
        };

        var unassignStaffBatch = function (id, data) {
            return Api.reservation.unassignStaffBatch(id, data).$promise;
        };

        var move = function (params, data) {
            return Api.reservation.move(params, data).$promise;
        };

        var arrive = function (params, data) {
            return Api.reservation.arrive(params, data).$promise;
        };

        var reactivateReservation = function (id) {
            return Api.reservation.reactivate({id: id}, {}).$promise;
        };

        var state = function (params, data) {
            return Api.reservation.state(params, data).$promise;
        };

        var tags = function (params, data) {
            return Api.reservation.tags(params, data).$promise;
        };

        var unassignTable = function (params, data) {
            return Api.reservation.unassign(params, data).$promise;
        };

        var getReservations = function (venueId, date, eventId) {
            if (angular.isObject(getReservationsReq) && angular.isFunction(getReservationsReq.$cancelRequest)) {
                getReservationsReq.$cancelRequest();
            }
            getReservationsReq = Api.reservations.getReservations({venueId: venueId, date: date, eventId: eventId});
            return getReservationsReq.$promise;
        };
        
        var getInfoList = function (venueId, date, bsType, eventId) {
            return Api.reservations.getInfoList(venueId, date, bsType, eventId).$promise;
        };

        return {
            createReservation: createReservation,
            updateReservation: updateReservation,
            deleteReservation: deleteReservation,
            getReservation: getReservation,
            approveReservation: approveReservation,
            assignStaffBatch: assignStaffBatch,
            assignTable: assignTable,
            guests: guests,
            unassignStaffBatch: unassignStaffBatch,
            move: move,
            arrive: arrive,
            reactivateReservation: reactivateReservation,
            state: state,
            tags: tags,
            unassignTable: unassignTable,
            getReservations: getReservations,
            getInfoList: getInfoList
        };
    }]);
}());