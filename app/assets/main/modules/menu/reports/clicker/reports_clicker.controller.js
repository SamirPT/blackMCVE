/**
 * Created by EGusev on 02.05.2016.
 */
(function () {
    'use strict';

    angular.module('mrblack.main').controller('ReportsClickerCtrl', ['$scope', 'ReportsService', '$state', 'Toastr', 'VenueService', 'UtilService', function ($scope, ReportsService, $state, Toastr, VenueService, UtilService) {
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
        $scope.monthNames = [
            "January", "February", "March",
            "April", "May", "June", "July",
            "August", "September", "October",
            "November", "December"
        ];
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

            ReportsService.getClickerReportPDF({dateFrom: dateFrom, dateTo: dateTo, venueId: $state.params.id, reportType: reportType}).then(
                function successCallback() {
                    $scope.pdfSpinner = true;
                    Toastr.success('', 'Success');
                },
                function errorCallback(error) {
                    $scope.pdfSpinner = true;
                    Toastr.error(error.data.error, 'Error');
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

            ReportsService.getClickerReport({dateFrom: dateFrom, dateTo: dateTo, venueId: $state.params.id, reportType: reportType}).then(
                function successCallback(response) {
                    getAmount(response, reportType);
                    $scope.spinner = true;
                },
                function errorCallback(error) {
                    $scope.spinner = true;
                    Toastr.error(error.data.error, 'Error');
                }
            );
        };

        var getAmount = function (resp, reportType) {
            if (reportType === REPORT_TYPE_DAILY) {
                var daySeries = {men: [], woman: [], total: []};
                $scope.womanAmount = 0;
                $scope.menAmount = 0;
                angular.forEach(resp, function (el) {
                    $scope.womanAmount += el.woman;
                    $scope.menAmount += el.men;
                    daySeries.men.push([new Date(el.date).getTime(), el.men]);
                    daySeries.woman.push([new Date(el.date).getTime(), el.woman]);
                    daySeries.total.push([new Date(el.date).getTime(), el.men + el.woman]);
                });
                daySeries.men = sortSeriesByDate(daySeries.men);
                daySeries.woman = sortSeriesByDate(daySeries.woman);
                daySeries.total = sortSeriesByDate(daySeries.total);
                createChart('dailyContainer', 'Daily Report for ' + $scope.date.getDate() + ' ' + $scope.monthNames[$scope.date.getMonth()], daySeries);
            }
            if (reportType === REPORT_TYPE_WEEKLY) {
                var weekSeries = {men: [], woman: [], total: []};
                $scope.womanAmountWeekly = 0;
                $scope.menAmountWeekly = 0;
                angular.forEach(resp, function (el) {
                    $scope.womanAmountWeekly += el.woman;
                    $scope.menAmountWeekly += el.men;
                    weekSeries.men.push([new Date(el.date).getTime(), el.men]);
                    weekSeries.woman.push([new Date(el.date).getTime(), el.woman]);
                    weekSeries.total.push([new Date(el.date).getTime(), el.men + el.woman]);
                });
                weekSeries.men = sortSeriesByDate(weekSeries.men);
                weekSeries.woman = sortSeriesByDate(weekSeries.woman);
                weekSeries.total = sortSeriesByDate(weekSeries.total);
                createChart('weeklyContainer', 'Weekly Report from ' + $scope.dateWeek.getDate() + ' ' + $scope.monthNames[$scope.dateWeek.getMonth()], weekSeries);
            }
            if (reportType === REPORT_TYPE_MONTHLY) {
                var monthSeries = {men: [], woman: [], total: []};
                $scope.womanAmountMonthly = 0;
                $scope.menAmountMonthly = 0;
                angular.forEach(resp, function (el) {
                    $scope.womanAmountMonthly += el.woman;
                    $scope.menAmountMonthly += el.men;
                    monthSeries.men.push([new Date(el.date).getTime(), el.men]);
                    monthSeries.woman.push([new Date(el.date).getTime(), el.woman]);
                    monthSeries.total.push([new Date(el.date).getTime(), el.men + el.woman]);
                });
                monthSeries.men = sortSeriesByDate(monthSeries.men);
                monthSeries.woman = sortSeriesByDate(monthSeries.woman);
                monthSeries.total = sortSeriesByDate(monthSeries.total);
                createChart('monthlyContainer', 'Monthly Report from ' + $scope.dateMonth.getDate() + ' ' + $scope.monthNames[$scope.dateMonth.getMonth()], monthSeries);
            }
            if (reportType === REPORT_TYPE_INTERVAL) {
                var intervalSeries = {men: [], woman: [], total: []};
                $scope.womanAmountInterval = 0;
                $scope.menAmountInterval = 0;
                angular.forEach(resp, function (el) {
                    $scope.womanAmountInterval += el.woman;
                    $scope.menAmountInterval += el.men;
                    intervalSeries.men.push([new Date(el.date).getTime(), el.men]);
                    intervalSeries.woman.push([new Date(el.date).getTime(), el.woman]);
                    intervalSeries.total.push([new Date(el.date).getTime(), el.men + el.woman]);
                });
                intervalSeries.men = sortSeriesByDate(intervalSeries.men);
                intervalSeries.woman = sortSeriesByDate(intervalSeries.woman);
                intervalSeries.total = sortSeriesByDate(intervalSeries.total);
                createChart('intervalContainer', 'Report from ' + $scope.dateRangeStart.getDate() + ' ' + $scope.monthNames[$scope.dateRangeStart.getMonth()] + ' to ' + $scope.dateRangeEnd.getDate() + ' ' + $scope.monthNames[$scope.dateRangeEnd.getMonth()], intervalSeries);
            }
        };
        
        var createChart = function (containerName, title, series) {
            return new Highcharts.Chart({
                chart: {
                    renderTo: document.getElementById(containerName),
                    type: 'spline'
                },
                title: {
                    text: title
                },
                xAxis: {
                    gridLineWidth: 1,
                    type: 'datetime'
                },
                yAxis: [{ // left y axis
                    title: {
                        text: null
                    },
                    labels: {
                        align: 'left',
                        x: 3,
                        y: 16,
                        format: '{value:.,0f}'
                    },
                    showFirstLabel: false
                }, { // right y axis
                    linkedTo: 0,
                    opposite: true,
                    title: {
                        text: null
                    },
                    labels: {
                        align: 'right',
                        x: -3,
                        y: 16,
                        format: '{value:.,0f}'
                    },
                    showFirstLabel: false
                }],
                legend: {
                    align: 'left',
                    verticalAlign: 'top',
                    y: 15,
                    floating: true,
                    borderWidth: 0
                },
                tooltip: {
                    shared: true,
                    crosshairs: true
                },
                plotOptions: {
                    series: {
                        cursor: 'pointer',
                        marker: {
                            lineWidth: 1
                        }
                    }
                },
                series: [
                    {
                        lineWidth: 4,
                        marker: {
                            radius: 4
                        },
                        color: '#5898E4',
                        name: 'Men',
                        data: series.men
                    },
                    {
                        lineWidth: 4,
                        marker: {
                            radius: 4
                        },
                        color: '#CD52BE',
                        name: 'Woman',
                        data: series.woman
                    },
                    {
                        lineWidth: 4,
                        marker: {
                            radius: 4
                        },
                        color: '#F3A505',
                        name: 'Total',
                        data: series.total
                    }
                ],
                credits: {
                    enabled: false
                }
            });
        };

        var sortSeriesByDate = function (array) {
            array.sort(function(a, b){
                var nameA=a[0], nameB=b[0];
                if (nameA > nameB) return 1;
                if (nameA < nameB) return -1;
                return 0;
            });
            return array;
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
    }]);
}());