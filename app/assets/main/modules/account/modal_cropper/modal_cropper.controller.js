/**
 * Created by EGusev on 11.04.2016.
 */
(function () {
    'use strict';

    angular.module('mrblack.main').controller('ModalCropperCtrl', ['$scope', '$modalInstance', 'dataUrl', 'file', '$timeout', 'Cropper', 'user', 'UserProfileService', 'Toastr', 'Upload', function($scope, $modalInstance, dataUrl, file, $timeout, Cropper, user, UserProfileService, Toastr, Upload) {
        $scope.loading = false;
        var data;
        $scope.user = user;
        $scope.dataUrl = dataUrl;
        $timeout(showCropper);

        $scope.options = {
            aspectRatio: 1,
            crop: function(dataNew) {
                data = dataNew;
            }
        };
        $scope.showEvent = 'show';
        function showCropper() { $scope.$broadcast($scope.showEvent); }

        $scope.cropImage = function() {
            if (!file || !data) return;
            Cropper.crop(file, data).then(Cropper.encode).then(function(dataUrl) {
                var pic = Cropper.decode(dataUrl);

                saveUserpic(pic);
            });
        };
        
        $scope.cancel = function() {
            $scope.loading = false;
            $modalInstance.dismiss('cancel');
        };

        var saveUserpic = function (file) {
            $scope.loading = true;

            file.upload = Upload.upload({
                url: 'images/userpic',
                data: {
                    image: file
                },
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
            file.upload.then(function (response) {
                $scope.user.userpic = response.data.response;
                $scope.loading = false;
                $modalInstance.dismiss('cancel');
                // UserProfileService.getUser().then(
                //     function successCallback(response) {
                //         $scope.user = response;
                //         $scope.loading = false;
                //         $modalInstance.dismiss('cancel');
                //     },
                //     function errorCallback() {
                //         Toastr.error('Updated profile reading error', 'Error');
                //     }
                // );
            }, function () {
                Toastr.error('Image upload failed', 'Error');
            });
        };
    }]);
}());