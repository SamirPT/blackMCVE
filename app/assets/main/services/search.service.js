/**
 * Created by EGusev on 21.04.2016.
 */
(function () {
   'use strict';

    angular.module('mrblack.main').service('SearchService', ['$resource', function ($resource) {
        var Api = $resource('/api/v1/search/users', {}, {
           getUser: {
               method: 'GET',
               isArray: true
           }
        });

        var getUser = function (namePart, phonePart) {
            return Api.getUser({namePart: namePart, phonePart: phonePart}).$promise;
        };
        
        return {
            getUser: getUser
        };
    }]);
}());