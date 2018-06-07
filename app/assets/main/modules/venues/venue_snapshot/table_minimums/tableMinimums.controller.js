/**
 * Created by EGusev on 06.06.2016.
 */
(function () {
    'use strict';
    angular.module('mrblack.main').controller('TableMinimumsCtrl', ['$scope', 'venueState', '$modalInstance', 'VenueService', 'date', 'eventId', 'Toastr', '$state', 'UtilService', function ($scope, venueState, $modalInstance, VenueService, date, eventId, Toastr, $state, UtilService) {
        $scope.venueState = venueState;

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.setMinimums = function () {
            VenueService.venueState({id: $state.params.id, date: UtilService.formatRequestDate(date), eventId: eventId}, $scope.venueState).then(
                function () {
                    $modalInstance.close($scope.venueState);
                },
                function (error) {
                    Toastr.error(error.data.error, 'Error');
                }
            );
        };
    }]);
}());