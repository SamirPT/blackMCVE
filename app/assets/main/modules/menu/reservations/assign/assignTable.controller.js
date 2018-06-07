/**
 * Created by EGusev on 24.05.2016.
 */
(function () {
    'use strict';

    angular.module('mrblack.main').controller('AssignTableController', ['$scope', '$modalInstance', 'Toastr', 'TablesService', '$state', 'reservation', 'reservationDate', 'ReservationService', 'eventId', function ($scope, $modalInstance, Toastr, TablesService, $state, reservation, reservationDate, ReservationService, eventId) {
        $scope.loading = false;
        $scope.reservation = reservation;
        $scope.bottleService = {
            TABLE: 'Table',
            STANDUP: 'Standup',
            BAR: 'Bar'
        };

        $scope.cancel = function () {
            $scope.loading = false;
            $modalInstance.dismiss('cancel');
        };

        var splitTables = function(response){
            $scope.tables = [];
            if (reservation.bottleService === 'TABLE') {
                angular.forEach(response, function(value) {
                    if ((value.tableInfo.bottleServiceType === 'TABLE' && !value.reservationInfo) || (value.tableInfo.bottleServiceType === 'TABLE' && value.reservationInfo.id === reservation.id)) {
                        $scope.tables.push(value);
                    }
                });
            }
            if (reservation.bottleService === 'STANDUP') {
                angular.forEach(response, function(value) {
                    if ((value.tableInfo.bottleServiceType === 'STANDUP' && !value.reservationInfo) || (value.tableInfo.bottleServiceType === 'STANDUP' && value.reservationInfo.id === reservation.id)) {
                        $scope.tables.push(value);
                    }
                });
            }
            if (reservation.bottleService === 'BAR') {
                angular.forEach(response, function(value) {
                    if ((value.tableInfo.bottleServiceType === 'BAR' && !value.reservationInfo) || (value.tableInfo.bottleServiceType === 'BAR' && value.reservationInfo.id === reservation.id)) {
                        $scope.tables.push(value);
                    }
                });
            }
        };

        var getTables = function () {
            TablesService.getSeating({venueId: $state.params.id, date: reservationDate, eventId: eventId}).then(
                function (response) {
                    splitTables(response);
                },
                function (response) {
                    if(response.data) {
                        Toastr.error(response.data.error, "Error");
                    }
                    $scope.cancel();
                }
            );
        };
        getTables();

        $scope.assignTable = function (table) {
            $scope.loading = true;
            $scope.thisId = table.tableInfo.id;
            ReservationService.assignTable({id: reservation.id}, table.tableInfo).then(
                function () {
                    $scope.loading = false;
                    $modalInstance.close(table);
                },
                function (error) {
                    $scope.loading = false;
                    Toastr.error(error.data.error, "Error");
                }
            );
        };

        $scope.unassignTable = function (table) {
            $scope.loading = true;
            $scope.thisId = table.tableInfo.id;
            ReservationService.unassignTable({id: reservation.id}).then(
                function () {
                    $scope.loading = false;
                    $modalInstance.close();
                },
                function (error) {
                    $scope.loading = false;
                    Toastr.error(error.data.error, "Error");
                }
            );
        };
    }]);
}());
