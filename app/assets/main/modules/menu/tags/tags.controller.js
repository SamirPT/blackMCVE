/**
 * Created by Eugen on 28.05.2016.
 */
(function () {
    'use strict';

    angular.module('mrblack.main').controller('TagsCtrl', ['$scope', 'VenueService', '$state', 'Toastr', function ($scope, VenueService, $state, Toastr) {

        var getTags = function () {
            VenueService.getTags({id: $state.params.id}).then(
                function (response) {
                    $scope.tagsList = response;
                },
                function (error) {
                    Toastr.error(error.data.error, 'Error');
                }
            );
        };

        getTags();

        $scope.createTag = function () {
            if ($scope.tagName !== undefined && $scope.tagName !== '') {
                VenueService.addTag({id: $state.params.id}, {tag: $scope.tagName}).then(
                    function () {
                        $scope.tagName = '';
                        getTags();
                    },
                    function (error) {
                        Toastr.error(error.data.error, 'Error');
                    }
                );
            } else {
                Toastr.error('Tag Name required', 'Error');
            }

        };

        $scope.deleteTag = function (tag) {
            VenueService.deleteTag({id: $state.params.id, tag: tag}).then(
                function () {
                    getTags();
                },
                function (error) {
                    Toastr.error(error.data.error, 'Error');
                }
            );
        };
    }]);
}());