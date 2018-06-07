/**
 * Created by EGusev on 02.05.2016.
 */
(function () {
    'use strict';

    angular.module('mrblack.main').service('ReportsService', ['$resource', function ($resource) {

        var Api = $resource('/api/v1/reports/:type/:pdf', {},
            {
                getClickerReport: {
                    method: 'GET',
                    params: {
                        type: 'clicker'
                    },
                    isArray: true
                },
                getClickerReportPDF: {
                    method: 'GET',
                    params: {
                        type: 'clicker',
                        pdf: 'pdf'
                    }
                },
                getEmployeesReport: {
                    method: 'GET',
                    params: {
                        type: 'employees'
                    }
                },
                getEmployeesReportPDF: {
                    method: 'GET',
                    params: {
                        type: 'employees',
                        pdf: 'pdf'
                    }
                },
                getPromotersReport: {
                    method: 'GET',
                    params: {
                        type: 'promoters'
                    }
                },
                getPromotersReportPDF: {
                    method: 'GET',
                    params: {
                        type: 'promoters',
                        pdf: 'pdf'
                    }
                },
                getReservationsReport: {
                    method: 'GET',
                    params: {
                        type: 'reservations'
                    }
                },
                getReservationsReportPDF: {
                    method: 'GET',
                    params: {
                        type: 'reservations',
                        pdf: 'pdf'
                    }
                }
            }
        );

        var getClickerReport = function (params) {
            return Api.getClickerReport(params).$promise;
        };

        var getClickerReportPDF = function (params) {
            return Api.getClickerReportPDF(params).$promise;
        };

        var getEmployeesReport = function (params) {
            return Api.getEmployeesReport(params).$promise;
        };

        var getEmployeesReportPDF = function (params) {
            return Api.getEmployeesReportPDF(params).$promise;
        };

        var getPromotersReport = function (params) {
            return Api.getPromotersReport(params).$promise;
        };

        var getPromotersReportPDF = function (params) {
            return Api.getPromotersReportPDF(params).$promise;
        };

        var getReservationsReport = function (params) {
            return Api.getReservationsReport(params).$promise;
        };

        var getReservationsReportPDF = function (params) {
            return Api.getReservationsReportPDF(params).$promise;
        };

        return {
            getClickerReport: getClickerReport,
            getClickerReportPDF: getClickerReportPDF,
            getEmployeesReport: getEmployeesReport,
            getEmployeesReportPDF: getEmployeesReportPDF,
            getPromotersReport: getPromotersReport,
            getPromotersReportPDF: getPromotersReportPDF,
            getReservationsReport: getReservationsReport,
            getReservationsReportPDF: getReservationsReportPDF
        };
    }]);
}());