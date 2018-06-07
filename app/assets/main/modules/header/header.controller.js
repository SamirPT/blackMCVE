/**
 * Created by EGusev on 14.04.2016.
 */
(function () {
    'use strict';

    angular.module('mrblack.main').controller('HeaderController', ['$scope', '$state', 'LogoutService', 'VenueService', 'Toastr', '$window', function ($scope, $state, LogoutService, VenueService, Toastr, $window) {
        $scope.logout = function () {
            LogoutService.logout().then(
                function successCallback() {
                    $window.location.href = '/';
                },
                function errorCallback() {
                    Toastr.error('Logout failed', 'Error');
                }
            );
        };

        $scope.clickLogo = function () {
            if ($state.params.id && !VenueService.getIsPromo()) {
                $state.go('venue_snapshot', {id: $state.params.id});
            } else {
                $state.go('my_venues');
            }
        };
    }]);
}());