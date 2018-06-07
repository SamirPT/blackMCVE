/**
 * Created by EGusev on 14.04.2016.
 */
(function () {
    'use strict';

    angular.module('mrblack.main').service('LogoutService', ['$resource', function($resource) {
        var Api = $resource('/logout',
            {}, {}
        );

        var logout = function () {
            return Api.get().$promise;
        };

        return {
            logout: logout
        };
    }]);
}());