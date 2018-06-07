(function () {
    'use strict';

    angular.module('mrblack.main').controller('EventsModalCtrl', ['$scope', '$modalInstance', 'dataUrl', 'file', '$timeout', 'Cropper', 'event', 'UserProfileService', 'Toastr', 'Upload', function($scope, $modalInstance, dataUrl, file, $timeout, Cropper, event, UserProfileService, Toastr, Upload) {
        $scope.loading = false;
        var data;
        $scope.event = event;
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
                url: '/images',
                data: {
                    image: file
                },
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
            file.upload.then(function (response) {
                $scope.event.pictureUrl = response.data.response;
                $scope.loading = false;
                $modalInstance.dismiss('cancel');
            }, function () {
                Toastr.error('Image upload failed', 'Error');
            });
        };
    }]);
}());