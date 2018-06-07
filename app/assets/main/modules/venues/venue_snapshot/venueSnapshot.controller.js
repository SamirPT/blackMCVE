/**
 * Created by Eugen on 04.06.2016.
 */
(function () {
    angular.module('mrblack.main').controller('VenueSnapshotCtrl', ['$scope', 'EventsService', 'VenueService', '$state', 'Toastr', '$modal', 'UtilService', function ($scope, EventsService, VenueService, $state, Toastr, $modal, UtilService) {
        $scope.spinner = true;
        $scope.changeEventSpinner = true;
        $scope.format = 'fullDate';
        $scope.currentEvent = EventsService.getCurrentEvent();
        $scope.date = $scope.currentEvent.date ? new Date($scope.currentEvent.date) : new Date();
        $scope.venueId = $state.params.id;
        $scope.permissions = VenueService.getPermissions();
        
        $scope.dateOptions = {
            startingDay: 1,
            showWeeks: false
        };

        $scope.changeDate = function (next) {
            $scope.spinner = false;
            if (next) {
                VenueService.getNextEventDate({
                    id: $state.params.id,
                    from: UtilService.formatRequestDate($scope.date)
                }).then(
                    function (response) {
                        $scope.date = new Date(response.date);
                        $scope.getEventsList();
                    },
                    function (error) {
                        Toastr.error(error.data.error, 'Error');
                        $scope.spinner = true;
                    }
                );
            } else {
                VenueService.getPrevEventDate({
                    id: $state.params.id,
                    from: UtilService.formatRequestDate($scope.date)
                }).then(
                    function (response) {
                        $scope.date = new Date(response.date);
                        $scope.getEventsList();
                    },
                    function (error) {
                        Toastr.error(error.data.error, 'Error');
                        $scope.spinner = true;
                    }
                );
            }
        };

        $scope.open = function($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened = true;
        };

        $scope.changeEvent = function () {
            $scope.getEventsList();
        };

        $scope.chooseEvent = function () {
            var modalInstance = $modal.open({
                templateUrl: '/assets/main/modules/menu/modal/event/event.modal.html',
                controller: 'EventModalCtrl',
                windowClass: 'move-tables',
                resolve: {
                    events: function() {
                        return $scope.events || [];
                    },
                    date: function() {
                        return $scope.date;
                    }
                }
            });

            modalInstance.result.then(
                function () {},
                function () {
                    if(!$scope.currentEvent.id) {
                        $scope.spinner = true;
                        Toastr.warning('Event was not selected', 'Warning');
                    } else {
                        getVenueSnapshot();
                    }
                }
            );
        };

        $scope.getEventsList = function () {
            $scope.changeEventSpinner = false;
            EventsService.getEvents({
                date: UtilService.formatRequestDate($scope.date),
                venueId: $state.params.id
            }).then(function (response) {
                $scope.events = response.eventsList;

                if($scope.events.length === 1) {
                    Toastr.info('', 'Only one event for this day');
                    EventsService.setCurrentEvent($scope.events[0]);
                    $scope.currentEvent = EventsService.getCurrentEvent();
                    getVenueSnapshot();
                }
                if ($scope.events.length > 1) {
                    $scope.chooseEvent();
                }
                $scope.changeEventSpinner = true;
            }, function (response) {
                if(response.data) {
                    Toastr.error(response.data.error, "Error");
                }
            });
        };

        var getVenueSnapshot = function () {
            VenueService.venueSnapshot({id: $state.params.id, date: UtilService.formatRequestDate($scope.date), eventId: $scope.currentEvent.id}).then(
                function (response) {
                    $scope.snapshot = response;
                },
                function (error) {
                    Toastr.error(error.data.error, 'Error');
                }
            );
            VenueService.getVenue({id: $state.params.id}).then(
                function (response) {
                    $scope.venueName = response.name;
                    $scope.spinner = true;
                },
                function (error) {
                    Toastr.error(error.data.error, 'Error');
                    $scope.spinner = true;
                }
            );
        };

        $scope.getEventsList();

        $scope.changeVenueState = function (type, closed) {
            if (type === 'gl') {
                $scope.snapshot.state.glClosed = closed;
            }
            if (type === 'bs') {
                $scope.snapshot.state.bsClosed = closed;
            }
            VenueService.venueState(
                {
                    id: $state.params.id,
                    date: UtilService.formatRequestDate($scope.date),
                    eventId: $scope.currentEvent.id
                }, $scope.snapshot.state
            ).then(
                function () {
                    
                },
                function (error) {
                    Toastr.error(error.data.error, 'Error');
                }
            );
        };

        $scope.setTableMinimums = function () {
            var modalInstance = $modal.open({
                templateUrl: '/assets/main/modules/venues/venue_snapshot/table_minimums/tableMinimums.html',
                controller: 'TableMinimumsCtrl',
                windowClass: 'menu-tables',
                resolve: {
                    venueState: function () {
                        return $scope.snapshot.state;
                    },
                    date: function () {
                        return $scope.date;
                    },
                    eventId: function () {
                        return $scope.currentEvent.id;
                    }
                }
            });

            modalInstance.result.then(
                function (state) {
                    $scope.snapshot.state = state;
                }
            );
        };
        
        $scope.createReservation = function (reservation) {
            $scope.spinner = false;
            var modalInstance = $modal.open({
                templateUrl: '/assets/main/modules/menu/reservations/reservation_details/reservation_details.html',
                controller: 'ReservationDetailsCtrl',
                windowClass: 'res-details-modal',
                resolve: {
                    reservation: function() {
                        return reservation;
                    },
                    reservationDate: function () {
                        return UtilService.formatRequestDate($scope.date);
                    },
                    eventId: function () {
                        return $scope.currentEvent.id;
                    },
                    table: {}
                }
            });

            modalInstance.result.then(
                function () {$scope.spinner = true;},
                function () {
                    getVenueSnapshot();
                    $scope.spinner = true;
                }
            );
        };

    }]);
}());