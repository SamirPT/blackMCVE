/**
 * Created by EGusev on 08.06.2016.
 */
(function () {
    
    'use strict';
    angular.module('mrblack.main').controller('VenueSettingsCtrl', ['$scope', 'UserProfileService', 'Toastr', '$state', 'VenueService', function ($scope, UserProfileService, Toastr, $state, VenueService) {
        $scope.permissions = VenueService.getPermissions();
        $scope.settings = {};
        
        $scope.closeVenueSettings = function () {
            document.getElementById('venueSettingsBox').style.display = 'none';
        };

        $scope.goToTagsView = function () {
            $scope.closeVenueSettings();
            $state.go('tags', {id: VenueService.utilGetVenueId()});    
        };

        UserProfileService.getSettings().then(
            function successCallback(response) {
                $scope.settings = response;
            },
            function errorCallback(error){
                Toastr.error(error.data.error, 'Error');
            }
        );    
    }]);    
}());