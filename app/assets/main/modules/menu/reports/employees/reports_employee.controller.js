/**
 * Created by EGusev on 03.05.2016.
 */
(function () {
    'use strict';

    angular.module('mrblack.main').controller('ReportsEmployeeCtrl', ['$scope', 'ReportsService', '$state', 'Toastr', 'VenueService', 'UtilService', function ($scope, ReportsService, $state, Toastr, VenueService, UtilService) {

        $scope.spinner = true;
        $scope.pdfSpinner = true;
        $scope.date = new Date();
        $scope.dateWeek = new Date();
        $scope.dateMonth = new Date();
        $scope.dateRangeStart = new Date();
        $scope.dateRangeEnd = new Date();
        $scope.format = 'fullDate';
        $scope.dateOptions = {
            startingDay: 1,
            showWeeks: false
        };
        var REPORT_TYPE_DAILY = 'DAILY';
        var REPORT_TYPE_WEEKLY = 'WEEKLY';
        var REPORT_TYPE_MONTHLY = 'MONTHLY';
        var REPORT_TYPE_INTERVAL = 'INTERVAL';

        $scope.getReportPDF = function (dateFrom, dateTo, reportType) {
            $scope.pdfSpinner = false;
            if (!!dateFrom) {
                dateFrom = UtilService.formatRequestDate(dateFrom);
            }
            if (!!dateTo) {
                dateTo = UtilService.formatRequestDate(dateTo);
            }
            
            ReportsService.getEmployeesReportPDF({dateFrom: dateFrom, dateTo: dateTo, venueId: $state.params.id, reportType: reportType}).then(
                function successCallback() {
                    $scope.pdfSpinner = true;
                    Toastr.success('', 'Success');
                },
                function errorCallback() {
                    $scope.pdfSpinner = true;
                    Toastr.error('', 'Error');
                }
            );
        };

        $scope.getReport = function(dateFrom, dateTo, reportType) {
            $scope.spinner = false;
            if (!!dateFrom) {
                dateFrom = UtilService.formatRequestDate(dateFrom);
            }
            if (!!dateTo) {
                dateTo = UtilService.formatRequestDate(dateTo);
            }

            ReportsService.getEmployeesReport({dateFrom: dateFrom, dateTo: dateTo, venueId: $state.params.id, reportType: reportType}).then(
                function successCallback(response) {
                    if (reportType === REPORT_TYPE_DAILY) {
                        $scope.employees = response.employees;
                        $scope.reservationItems = response.reservationItems;

                        $scope.sortedItems = sorting($scope.reservationItems);
                        tableCalculation($scope.reservationItems, reportType);
                    }
                    if (reportType === REPORT_TYPE_WEEKLY) {
                        $scope.employeesWeekly = response.employees;
                        $scope.reservationItemsWeekly = response.reservationItems;

                        $scope.sortedItemsWeekly = sorting($scope.reservationItemsWeekly);
                        tableCalculation($scope.reservationItemsWeekly, reportType);
                    }
                    if (reportType === REPORT_TYPE_MONTHLY) {
                        $scope.employeesMonthly = response.employees;
                        $scope.reservationItemsMonthly = response.reservationItems;

                        $scope.sortedItemsMonthly = sorting($scope.reservationItemsMonthly);
                        tableCalculation($scope.reservationItemsMonthly, reportType);
                    }
                    if (reportType === REPORT_TYPE_INTERVAL) {
                        $scope.employeesInterval = response.employees;
                        $scope.reservationItemsInterval = response.reservationItems;

                        $scope.sortedItemsInterval = sorting($scope.reservationItemsInterval);
                        tableCalculation($scope.reservationItemsInterval, reportType);
                    }
                    $scope.spinner = true;
                },
                function errorCallback() {
                    $scope.spinner = true;
                    Toastr.error('', 'Error');
                }
            );
        };

        $scope.open = function($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened = true;
        };

        $scope.openSecondCalendar = function($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened2 = true;
        };

        $scope.getNextDate = function (reportType, dateFrom, dateTo) {
            $scope.spinner = false;

            if (reportType === REPORT_TYPE_DAILY) {
                VenueService.getNextEventDate({
                    id: $state.params.id,
                    from: UtilService.formatRequestDate($scope.date)
                }).then(
                    function (response) {
                        $scope.date = new Date(response.date);
                        $scope.getReport(null, $scope.date, reportType);
                    },
                    function (error) {
                        $scope.spinner = true;
                        Toastr.error(error.data.error, 'Error');
                    }
                );
            }

            if (reportType === REPORT_TYPE_WEEKLY) {
                VenueService.getNextEventDate({
                    id: $state.params.id,
                    from: UtilService.formatRequestDate($scope.dateWeek)
                }).then(
                    function (response) {
                        $scope.dateWeek = new Date(response.date);
                        $scope.getReport(null, $scope.dateWeek, reportType);
                    },
                    function (error) {
                        $scope.spinner = true;
                        Toastr.error(error.data.error, 'Error');
                    }
                );
            }

            if (reportType === REPORT_TYPE_MONTHLY) {
                VenueService.getNextEventDate({
                    id: $state.params.id,
                    from: UtilService.formatRequestDate($scope.dateMonth)
                }).then(
                    function (response) {
                        $scope.dateMonth = new Date(response.date);
                        $scope.getReport(null, $scope.dateMonth, reportType);
                    },
                    function (error) {
                        $scope.spinner = true;
                        Toastr.error(error.data.error, 'Error');
                    }
                );
            }

            if (reportType === REPORT_TYPE_INTERVAL) {
                if (!!dateFrom) {
                    VenueService.getNextEventDate({
                        id: $state.params.id,
                        from: UtilService.formatRequestDate($scope.dateRangeStart)
                    }).then(
                        function (response) {
                            $scope.dateRangeStart = new Date(response.date);
                            $scope.getReport($scope.dateRangeStart, $scope.dateRangeEnd, reportType);
                        },
                        function (error) {
                            $scope.spinner = true;
                            Toastr.error(error.data.error, 'Error');
                        }
                    );
                }
                if (!!dateTo) {
                    VenueService.getNextEventDate({
                        id: $state.params.id,
                        from: UtilService.formatRequestDate($scope.dateRangeEnd)
                    }).then(
                        function (response) {
                            $scope.dateRangeEnd = new Date(response.date);
                            $scope.getReport($scope.dateRangeStart, $scope.dateRangeEnd, reportType);
                        },
                        function (error) {
                            $scope.spinner = true;
                            Toastr.error(error.data.error, 'Error');
                        }
                    );
                }
            }
        };

        $scope.getPrevDate = function (reportType, dateFrom, dateTo) {
            $scope.spinner = false;

            if (reportType === REPORT_TYPE_DAILY) {
                VenueService.getPrevEventDate({
                    id: $state.params.id,
                    from: UtilService.formatRequestDate($scope.date)
                }).then(
                    function (response) {
                        $scope.date = new Date(response.date);
                        $scope.getReport(null, $scope.date, reportType);
                    },
                    function (error) {
                        $scope.spinner = true;
                        Toastr.error(error.data.error, 'Error');
                    }
                );
            }

            if (reportType === REPORT_TYPE_WEEKLY) {
                VenueService.getPrevEventDate({
                    id: $state.params.id,
                    from: UtilService.formatRequestDate($scope.dateWeek)
                }).then(
                    function (response) {
                        $scope.dateWeek = new Date(response.date);
                        $scope.getReport(null, $scope.dateWeek, reportType);
                    },
                    function (error) {
                        $scope.spinner = true;
                        Toastr.error(error.data.error, 'Error');
                    }
                );
            }

            if (reportType === REPORT_TYPE_MONTHLY) {
                VenueService.getPrevEventDate({
                    id: $state.params.id,
                    from: UtilService.formatRequestDate($scope.dateMonth)
                }).then(
                    function (response) {
                        $scope.dateMonth = new Date(response.date);
                        $scope.getReport(null, $scope.dateMonth, reportType);
                    },
                    function (error) {
                        $scope.spinner = true;
                        Toastr.error(error.data.error, 'Error');
                    }
                );
            }

            if (reportType === REPORT_TYPE_INTERVAL) {
                if (!!dateFrom) {
                    VenueService.getPrevEventDate({
                        id: $state.params.id,
                        from: UtilService.formatRequestDate($scope.dateRangeStart)
                    }).then(
                        function (response) {
                            $scope.dateRangeStart = new Date(response.date);
                            $scope.getReport($scope.dateRangeStart, $scope.dateRangeEnd, reportType);
                        },
                        function (error) {
                            $scope.spinner = true;
                            Toastr.error(error.data.error, 'Error');
                        }
                    );
                }
                if (!!dateTo) {
                    VenueService.getPrevEventDate({
                        id: $state.params.id,
                        from: UtilService.formatRequestDate($scope.dateRangeEnd)
                    }).then(
                        function (response) {
                            $scope.dateRangeEnd = new Date(response.date);
                            $scope.getReport($scope.dateRangeStart, $scope.dateRangeEnd, reportType);
                        },
                        function (error) {
                            $scope.spinner = true;
                            Toastr.error(error.data.error, 'Error');
                        }
                    );
                }
            }
        };

        var tableCalculation = function (reservArray, reportType) {
            var emptyTableObject = {
                bookedResos: 0,
                actualResos: 0,
                bookedGuests: 0,
                actualGuests: 0,
                bookedSpend: 0,
                actualSpend: 0
            };
            angular.forEach(reservArray, function (el) {
                if (el.bottleServiceType) {
                    emptyTableObject.bookedResos += 1;
                }
                if (el.isActualReservation) {
                    emptyTableObject.actualResos += 1;
                }
                emptyTableObject.bookedGuests += el.guestsBooked;
                emptyTableObject.actualGuests += el.guestsActual;
                emptyTableObject.bookedSpend += el.minSpend;
                emptyTableObject.actualSpend += el.actualSpent;
            });

            if (reportType === REPORT_TYPE_DAILY) {
                $scope.tableDay = emptyTableObject;
            }
            if (reportType === REPORT_TYPE_WEEKLY) {
                $scope.tableWeek = emptyTableObject;
            }
            if (reportType === REPORT_TYPE_MONTHLY) {
                $scope.tableMonth = emptyTableObject;
            }
            if (reportType === REPORT_TYPE_INTERVAL) {
                $scope.tableInterval = emptyTableObject;
            }
        };

        var sorting = function (array) {
            var map = {};
            if (array.length === 1) {
                map[array[0].date] = [];
                map[array[0].date].push(array[0]);
            } else {
                array.sort(function(a, b){
                    var nameA=a.date, nameB=b.date;
                    if (nameA > nameB) return 1;
                    if (nameA < nameB) return -1;
                    return 0;
                });

                angular.forEach(array, function (el) {
                    if (!map.hasOwnProperty(el.date)) {
                        map[el.date] = [];
                    }
                    map[el.date].push(el);
                });
            }
            return map;
        };

        $scope.clearDayFilter = function () {
            $scope.selectedEmployee = '';
        };

        $scope.clearWeekFilter = function () {
            $scope.selectedEmployeeWeek = '';
        };

        $scope.clearMonthFilter = function () {
            $scope.selectedEmployeeMonth = '';
        };

        $scope.clearIntervalFilter = function () {
            $scope.selectedEmployeeInterval = '';
        };
    }]);
}());