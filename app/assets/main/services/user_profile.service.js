/**
 * Created by egusev on 06.04.2016.
 */
(function () {
    'use strict';

    angular.module('mrblack.main').service('UserProfileService', ['$resource', function($resource) {

        var Api = $resource('/api/v1/user/:id/:action',
            {}, {
                delete: {
                    method: 'DELETE'
                },
                get: {
                    method: 'GET'
                },
                create: {
                    method:'POST'
                },
                update: {
                    method:'PUT'
                },
                getSettings: {
                    method: 'GET',
                    params: {
                        action: 'settings'
                    }
                },
                updateSettings: {
                    method: 'PUT',
                    params: {
                        action: 'settings'
                    }
                },
                updateCustomerInfo: {
                    method: 'PUT',
                    params: {
                        action: 'customerInfo'
                    }
                },
                getCustomerInfo: {
                    method: 'GET',
                    params: {
                        action: 'customerInfo'
                    }
                }
            });

        var deleteUser = function () {
            return Api.delete().$promise;
        };

        var getUser = function () {
            return Api.get().$promise;
        };
        
        var createUser = function (user) {
            return Api.create(user).$promise;
        };

        var updateUser = function (user) {
            return Api.update(user).$promise;
        };
        
        var getSettings = function () {
            return Api.getSettings().$promise;
        };

        var updateSettings = function (settings) {
            return Api.updateSettings(settings).$promise;
        };

        var updateCustomerInfo = function (params, data) {
            return Api.updateCustomerInfo(params, data).$promise;
        };

        var getCustomerInfo = function (params, data) {
            return Api.getCustomerInfo(params, data).$promise;
        };

        var Dict = $resource('/api/v1/dict/:type',
            {}, {
                getPromoters: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        type: 'promoters'
                    }
                },
                getRoles: {
                    method: 'GET',
                    params: {
                        type: 'roles'
                    }
                }
            });

        var getPromoters = function () {
            return Dict.getPromoters().$promise;
        };

        var getRoles = function () {
            return Dict.getRoles().$promise;
        };

        return {
            deleteUser: deleteUser,
            getUser: getUser,
            createUser: createUser,
            updateUser: updateUser,
            getSettings: getSettings,
            updateSettings: updateSettings,
            updateCustomerInfo: updateCustomerInfo,
            getCustomerInfo: getCustomerInfo,
            getPromoters: getPromoters,
            getRoles: getRoles
        };
    }]);
}());