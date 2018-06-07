/**
 * Created by EGusev on 27.05.2016.
 */
(function () {
    'use strict';

    angular.module('mrblack.main').controller('MoveReservationCtrl', ['$scope', '$modalInstance', 'reservation', 'Toastr', 'ReservationService', 'date', 'UtilService', function ($scope, $modalInstance, reservation, Toastr, ReservationService, date, UtilService) {
        $scope.loading = false;
        $scope.moveDate = date;

        $scope.open = function($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened = true;
        };

        $scope.cancel = function () {
            $scope.loading = false;
            $modalInstance.dismiss('cancel');
        };

        $scope.moveReservation = function () {
            var formattedDate = UtilService.formatRequestDate($scope.moveDate);

            ReservationService.move({
                id: reservation.id,
                newDate: formattedDate
            }).then(function () {
                Toastr.success("Reservation moved to " + formattedDate, "Success");
                $modalInstance.close(formattedDate);
            }, function (error) {
                Toastr.error(error.data.error, "Error");
            });
        };
    }]);
}());
