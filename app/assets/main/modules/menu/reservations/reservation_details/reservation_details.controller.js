/**
 * Created by EGusev on 19.04.2016.
 */
(function () {
   'use strict';

    angular.module('mrblack.main').controller('ReservationDetailsCtrl', ['$scope', '$modal', '$modalInstance', 'reservation', 'SearchService', 'Toastr', 'ReservationService', '$state', 'reservationDate', 'eventId', 'VenueService', 'table', function ($scope, $modal, $modalInstance, reservation, SearchService, Toastr, ReservationService, $state, reservationDate, eventId, VenueService, table) {
        $scope.loading = false;
        $scope.isNewReservation = !reservation;
        $scope.tagsAccordionOpen = false;
        $scope.secondAccordionOpen = false;
        $scope.table = table;
        $scope.permissions = VenueService.getPermissions();
        $scope.hasTable = false;

        if ($scope.isNewReservation) {
            $scope.reservation = {};
            $scope.reservation.bottleService = $scope.table ? $scope.table.tableInfo.bottleServiceType : '';
            $scope.header = 'Create new reservation';
            $scope.btnTitle = 'Add reservation';
            $scope.reservation.tags = [];
            if (!!$scope.table) {
                $scope.bsSelector = 'bs';
                $scope.hasTable = true;
            } else {
                $scope.bsSelector = 'gl';
            }
        } else {
            $scope.searchResult = {
                id: reservation.guestInfo.id,
                name: reservation.guestInfo.fullName,
                phone: reservation.guestInfo.phoneNumber
            };
            if (!!reservation.arrivalTime) {
                var arrTime = new Date(reservation.arrivalTime);
                $scope.arrivalTime = arrTime.getDate() + "." + (arrTime.getMonth()+1) + "." + arrTime.getFullYear() + " " + arrTime.getHours() + ":" + arrTime.getMinutes() + ":" + arrTime.getSeconds();
            }
            $scope.reservation = reservation;
            $scope.header = 'Reservation details';
            $scope.btnTitle = 'Change';
            if (!!$scope.reservation.bottleService || !!$scope.table) {
                $scope.bsSelector = 'bs';
            } else {
                $scope.bsSelector = 'gl';
            }
        }

        $scope.cancel = function () {
            $scope.loading = false;
            $modalInstance.dismiss('cancel');
        };
        
        $scope.feedbacksByUser = function (reservation) {
            $state.go('feedbacks', {
                id: $state.params.id,
                reservationId: reservation.id,
                userId: reservation.guestInfo.id,
                guestInfo: reservation.guestInfo
            });
            $modalInstance.dismiss('cancel');
        };

        $scope.feedbacksByReservation = function (reservation) {
            $state.go('feedbacks', {
                id: $state.params.id,
                reservationId: reservation.id,
                guestInfo: reservation.guestInfo
            });
            $modalInstance.dismiss('cancel');
        };

        $scope.searchResults = [];

        $scope.searchUserByName = function (inputed) {
            if(inputed !== '') {
                SearchService.getUser(inputed, null).then(
                    function successCallback(response) {
                        $scope.searchResults = response;
                    }
                );
            }
        };

        $scope.searchUserByPhone = function (inputed) {
            if(inputed !== '') {
                SearchService.getUser(null, inputed).then(
                    function successCallback(response) {
                        $scope.searchResults = response;
                    }
                );
            }
        };

        $scope.onSelect = function ($item) {
            $scope.searchResult = $item;
        };

        $scope.deleteReservation = function (id) {
            ReservationService.deleteReservation(id).then(
                function () {
                    $modalInstance.dismiss('cancel');
                    Toastr.success('Reservation deleted', 'Success');
                },
                function () {
                    Toastr.error('Reservation deleting failed', 'Error');
                }
            );
        };

        var assignTable = function (reservationId, table) {
            ReservationService.assignTable({
                id: reservationId
            },{
                id: table.tableInfo.id
            }).then(function (response) {
                pushTags(reservationId);
            }, function (response) {
                Toastr.error(response.data.error, "Error");
                pushTags(reservationId);
            });
        };

        $scope.setCmplGirls = function () {
            $scope.reservation.complimentGirlsQty = 0;
        };

        $scope.setCmplGuys = function () {
            $scope.reservation.complimentGuysQty = 0;
        };

        $scope.setReducedGirls = function () {
            $scope.reservation.reducedGirlsQty = 0;
        };

        $scope.setReducedGuys = function () {
            $scope.reservation.reducedGuysQty = 0;
        };

        $scope.createReservation = function () {
            $scope.loading = true;
            if($scope.isNewReservation) {
                if ($scope.searchResult === undefined) {
                    $scope.loading = false;
                    Toastr.error('Guest name required', 'Error');
                } else {
                    var reservation = {
                        guestInfo: {
                            id: $scope.searchResult.id,
                            fullName: $scope.searchResult.name,
                            phoneNumber: $scope.searchResult.phone
                        },
                        guestsNumber: $scope.reservation.guestsNumber,
                        notifyMgmtOnArrival: !!$scope.reservation.notifyMgmtOnArrival,
                        bookingNote: $scope.reservation.bookingNote,
                        complimentGirls: !!$scope.reservation.complimentGirls,
                        complimentGirlsQty: $scope.reservation.complimentGirlsQty || 0,
                        complimentGuys: !!$scope.reservation.complimentGuys,
                        complimentGuysQty: $scope.reservation.complimentGuysQty || 0,
                        reducedGirls: !!$scope.reservation.reducedGirls,
                        reducedGirlsQty: $scope.reservation.reducedGirlsQty || 0,
                        reducedGuys: !!$scope.reservation.reducedGuys,
                        reducedGuysQty: $scope.reservation.reducedGuysQty || 0,
                        mustEnter: $scope.reservation.mustEnter,
                        groupType: $scope.reservation.groupType,
                        bottleMin: $scope.reservation.bottleMin,
                        minSpend: $scope.reservation.minSpend,
                        totalSpent: $scope.reservation.totalSpent,
                        bottleService: $scope.reservation.bottleService || null,
                        venueId: $state.params.id,
                        reservationDate: reservationDate,
                        eventId: eventId,
                        estimatedArrivalTime: $scope.reservation.estimatedArrivalTime,
                        tags: $scope.reservation.tags
                    };

                    ReservationService.createReservation(reservation).then(
                        function successCallback(response) {
                            if ($scope.table) {
                                assignTable(response.id, $scope.table);
                            }
                            $scope.loading = false;
                            $scope.cancel();
                        },
                        function errorCallback(error) {
                            $scope.loading = false;
                            Toastr.error(error.data.error, 'Error');
                        }
                    );
                }
            } else {
                $scope.reservation.complimentGirls = !!$scope.reservation.complimentGirls;
                $scope.reservation.complimentGirlsQty = $scope.reservation.complimentGirlsQty || 0;
                $scope.reservation.complimentGuys = !!$scope.reservation.complimentGuys;
                $scope.reservation.complimentGuysQty = $scope.reservation.complimentGuysQty || 0;
                $scope.reservation.reducedGirls = !!$scope.reservation.reducedGirls;
                $scope.reservation.reducedGirlsQty = $scope.reservation.reducedGirlsQty || 0;
                $scope.reservation.reducedGuys = !!$scope.reservation.reducedGuys;
                $scope.reservation.reducedGuysQty = $scope.reservation.reducedGuysQty || 0;
                $scope.reservation.bottleService = $scope.reservation.bottleService || 'NONE';
                ReservationService.updateReservation($scope.reservation).then(
                    function successCallback() {
                        $scope.loading = false;
                        $scope.cancel();
                    },
                    function errorCallback(error) {
                        $scope.loading = false;
                        Toastr.error(error.data.error, 'Error');
                    }
                );
            }
        };

        $scope.addTags = function () {
            var modalInstance = $modal.open({
                templateUrl: '/assets/main/modules/menu/reservations/tags/tags.html',
                controller: 'AddTagsCtrl',
                windowClass: 'menu-tables',
                resolve: {
                    reservation: function () {
                        return $scope.reservation;
                    }
                }
            });
        };

        $scope.removeTag = function (tag) {
            $scope.reservation.tags.splice($scope.reservation.tags.indexOf(tag), 1);
        };

        $scope.completeReservation = function (permission, reservation) {
            var modalInstance = $modal.open({
                templateUrl: '/assets/main/modules/menu/reservations/complete/complete.html',
                controller: 'CompleteResCtrl',
                resolve: {
                    permission: function () {
                        return permission;
                    },
                    reservation: function () {
                        return reservation;
                    }
                }
            });
            $scope.cancel();
        };
    }]);
}());