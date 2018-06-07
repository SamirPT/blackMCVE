/**
 * Created by egusev on 05.04.2016.
 */
(function () {
    'use strict';

    angular.module('mrblack.main').controller('AccountCtrl', ['$scope', 'UserProfileService', 'Cropper', '$modal', 'Toastr', '$window', 'DictService', function ($scope, UserProfileService, Cropper, $modal, Toastr, $window, DictService) {
        var monthNames = [
            "January", "February", "March",
            "April", "May", "June", "July",
            "August", "September", "October",
            "November", "December"
        ];

        UserProfileService.getUser().then(
            function successCallback(response){
                $scope.user = response;

                var bth = new Date($scope.user.birthday);
                var day = bth.getDate();
                var monthIndex = bth.getMonth();
                var year = bth.getFullYear();
                $scope.bth = day + ' ' + monthNames[monthIndex] + ' ' + year;

                if($scope.user.promotionCompany){
                    $scope.promotionCompanyId = $scope.user.promotionCompany.id.toString();
                }

                DictService.getRoles().then(
                    function successCallback(response) {
                        $scope.roles = response;
                        $scope.preferred = {};
                        angular.forEach($scope.user.preferredRoles, function(role){
                            $scope.preferred[role] = role;
                        });
                    },
                    function errorCallback() {
                        Toastr.error('Roles list loading failed', 'Error');
                    }
                );
            },
            function errorCallback(){
                Toastr.error('Profile loading failed', 'Error');
            });
        UserProfileService.getSettings().then(
            function successCallback(response) {
                $scope.settings = response;
            },
            function errorCallback(){
                Toastr.error('Profile settings loading failed', 'Error');
            }
        );
        UserProfileService.getPromoters().then(
            function successCallback(response) {
                $scope.promoters = response;
            },
            function errorCallback() {
                Toastr.error('Promoters list loading failed', 'Error');
            }
        );

        $scope.onFile = function(blob) {
            var file;
            Cropper.encode((file = blob)).then(function(dataUrl) {
                var modalInstance = $modal.open({
                    templateUrl: '/assets/main/modules/account/modal_cropper/modal_cropper.html',
                    controller: 'ModalCropperCtrl',
                    windowClass: 'big-modal',
                    resolve: {
                        file: function () {
                            return file;
                        },
                        dataUrl: function() {
                            return dataUrl;
                        },
                        user: function () {
                            return $scope.user;
                        }
                    }
                });
            });
        };

        $scope.checkRole = function(role) {
            if($scope.preferred[role]){
                $scope.preferred[role] = '';
                var ind = $scope.user.preferredRoles.indexOf(role);
                if (ind !== -1) {
                    $scope.user.preferredRoles.splice(ind, 1);
                }
            } else {
                $scope.preferred[role] = role;
                $scope.user.preferredRoles.push(role);
            }
        };
        
        $scope.updateProfile = function () {
            if($scope.user.promotionCompany){
                $scope.user.promotionCompany = {
                    id: parseInt($scope.promotionCompanyId)
                };
            }
            UserProfileService.updateUser($scope.user).then(
                function successCallback() {
                    Toastr.success('Profile successfully updated', 'Success');
                },
                function errorCallback() {
                    Toastr.error('Profile saving failed', 'Error');
                }
            );
            UserProfileService.updateSettings($scope.settings).then(
                function successCallback() {
                    Toastr.success('Profile settings successfully updated', 'Success');
                },
                function errorCallback() {
                    Toastr.error('Profile settings saving failed', 'Error');
                }
            );
        };
        
        $scope.deleteProfile = function () {
            UserProfileService.deleteUser().then(
                function successCallback() {
                    $window.location.href = '/';
                    Toastr.success('Profile successfully deleted', 'Success');
                },
                function errorCallback() {
                    Toastr.error('Profile deletion failed', 'Error');
                }
            );
        };
    }]);
}());