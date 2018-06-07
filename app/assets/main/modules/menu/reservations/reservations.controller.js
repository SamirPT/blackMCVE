(function () {
	'use strict';

	angular.module('mrblack.main').controller('ReservationsCtrl', ['$scope', '$state', 'VenueService', 'Toastr', 'ReservationService', '$modal', 'EventsService', 'UtilService', function ($scope, $state, VenueService, Toastr, ReservationService, $modal, EventsService, UtilService) {
		$scope.state = $state;
		$scope.spinner = true;
		$scope.changeEventSpinner = true;
		$scope.format = 'fullDate';
		$scope.currentEvent = EventsService.getCurrentEvent();
		$scope.date = $scope.currentEvent.date ? new Date($scope.currentEvent.date) : new Date();
		$scope.activeTab = $state.params.tab;
		$scope.dateOptions = {
			startingDay: 1,
			showWeeks: false
		};
		$scope.permissions = VenueService.getPermissions();

		$scope.groupType = {
			MIX: 'Mix',
			ALL_GIRLS: 'All Girls',
			UNKNOWN: 'Unknown',
			PROMO: 'Promo',
			ALL_GUYS: 'All Guys',
			OWNER: 'Owner'
		};

		$scope.bottleServiceTypes = {
			TABLE: 'Table',
			STANDUP: 'Standup',
			BAR: 'Bar'
		};
		
		$scope.statuses = {
			RELEASED: 'Released',
			COMPLETED: 'Completed',
			NO_SHOW: 'No Show',
			APPROVED: 'Approved',
			ARRIVED: 'Arrived',
			PRE_RELEASED: 'Pre Released',
			CONFIRMED_COMPLETE: 'Confirmed'
		};
		
		var getReservationsPerDay = function(date) {
			$scope.spinner = false;
			var month = (date.getMonth() + 1).toString();
			if(month.length == 1) {
				month = '0' + month;
			}
			var day = date.getDate().toString();
			if(day.length == 1) {
				day = '0' + day;
			}
			$scope.currentReservationDate = date.getFullYear() + '-' + month + '-' + day;

			ReservationService.getReservations($state.params.id, $scope.currentReservationDate, $scope.currentEvent.id).then(
				function successCallback(response) {
					sorting(response.guestlist, 'guest');
					sorting(response.pending, 'pending');
					sorting(response.approved, 'approved');
					$scope.spinner = true;
				},
				function errorCallback() {
					$scope.spinner = true;
					Toastr.error('Reservations loading failed', 'Error');
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
					getReservationsPerDay($scope.date);
				} 
				if ($scope.events.length > 1) {
					$scope.chooseEvent();
				}
				$scope.changeEventSpinner = true;
			}, function () {
				Toastr.error('Events list loading failed', "Error");
			});
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
						getReservationsPerDay($scope.date);
					}
				}
			);
		};
		
		$scope.changeEvent = function () {
			$scope.getEventsList();
		};

		$scope.getEventsList();
		
		$scope.changeReservationState = function (id, newState) {
			ReservationService.state({id: id, newState: newState}).then(
				function successCallback() {
					ReservationService.getReservations($state.params.id, $scope.currentReservationDate, $scope.currentEvent.id).then(
						function successCallback(response) {
							sorting(response.pending, 'pending');
							sorting(response.approved, 'approved');
						},
						function errorCallback() {
							Toastr.error('Reservations loading failed', 'Error');
						}
					);
				},
				function errorCallback(error) {
					Toastr.error(error.data.error, 'Error');
				}
			);
		};

		$scope.declineReservation = function (id) {
			ReservationService.deleteReservation(id).then(
				function () {
					ReservationService.getReservations($state.params.id, $scope.currentReservationDate, $scope.currentEvent.id).then(
						function successCallback(response) {
							sorting(response.pending, 'pending');
							sorting(response.approved, 'approved');
						},
						function errorCallback() {
							Toastr.error('Reservations loading failed', 'Error');
						}
					);
					Toastr.success('Reservation declined', 'Success');
				},
				function () {
					Toastr.error('Reservation decline failed', 'Error');
				}
			);
		};

		$scope.clickerGuests = function (reservation, gender, action) {
			$scope.guestSpinner = true;
			$scope.thisId = reservation.id;
			ReservationService.guests({id: reservation.id, action: action, gender: gender}).then(
				function () {
					ReservationService.getReservation(reservation.id).then(
						function successCallback(response) {
							reservation.arrivedGirls = response.arrivedGirls;
							reservation.arrivedGuys = response.arrivedGuys;
						},
						function errorCallback(error) {
							Toastr.error(error.data.error, 'Error');
						}
					);
					$scope.guestSpinner = false;
				},
				function (error) {
					Toastr.error(error.data.error, 'Error');
					$scope.guestSpinner = false;
				}
			);
		};

		$scope.activateReservation = function (id) {
			ReservationService.reactivateReservation(id).then(
				function successCallback() {
					Toastr.success('Reservation reactivated', 'Success');
					ReservationService.getReservations($state.params.id, $scope.currentReservationDate, $scope.currentEvent.id).then(
						function successCallback(response) {
							sorting(response.guestlist, 'guest');
							sorting(response.pending, 'pending');
							sorting(response.approved, 'approved');
						},
						function errorCallback() {
							Toastr.error('Reservations loading failed', 'Error');
						}
					);
				},
				function errorCallback() {
					Toastr.error('Reservation reactivating failed', 'Error');
				}
			);
		};

		$scope.assignTable = function (reservation) {
			var modalInstance = $modal.open({
				templateUrl: '/assets/main/modules/menu/reservations/assign/assignTable.html',
				controller: 'AssignTableController',
				windowClass: 'big-modal',
				resolve: {
					reservation: function() {
						return reservation;
					},
					reservationDate: function () {
						return $scope.currentReservationDate;
					},
					eventId: function () {
						return $scope.currentEvent.id;
					}
				}
			});

			modalInstance.result.then(
				function (table) {
					if (table) {
						reservation.tableInfo = table.tableInfo;
					} else {
						reservation.tableInfo = table;
					}
				}	
			);
		};
		
		$scope.moveReservation = function (reservation) {
			var modalInstance = $modal.open({
				templateUrl: '/assets/main/modules/menu/reservations/move/moveReservation.html',
				controller: 'MoveReservationCtrl',
				windowClass: 'move-tables',
				resolve: {
					reservation: function () {
						return reservation;
					},
					date: function () {
						return $scope.date;
					}
				}
			});
			
			modalInstance.result.then(
				function () {
					$scope.spinner = false;
					ReservationService.getReservations($state.params.id, $scope.currentReservationDate, $scope.currentEvent.id).then(
						function successCallback(response) {
							sorting(response.approved, 'approved');
							$scope.spinner = true;
						},
						function errorCallback() {
							$scope.spinner = true;
							Toastr.error('Reservations loading failed', 'Error');
						}
					);
				}
			);
		};
		
		$scope.createReservation = function (reservation) {
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
				function () {},
				function () {
					ReservationService.getReservations($state.params.id, $scope.currentReservationDate, $scope.currentEvent.id).then(
						function successCallback(response) {
							sorting(response.guestlist, 'guest');
							sorting(response.pending, 'pending');
						},
						function errorCallback() {
							$scope.spinner = true;
							Toastr.error('Reservations loading failed', 'Error');
						}
					);
				}
			);
		};
		
		$scope.reservationEditing = function (reservation, canViewAndModifyAllGuestListResos) {
			if (canViewAndModifyAllGuestListResos || $scope.permissions.canViewAndModifyAllReservations) {
				ReservationService.getReservation(reservation.id).then(
					function successCallback(currentReservation) {

						var modalInstance = $modal.open({
							templateUrl: '/assets/main/modules/menu/reservations/reservation_details/reservation_details.html',
							controller: 'ReservationDetailsCtrl',
							windowClass: 'res-details-modal',
							resolve: {
								reservation: function() {
									return currentReservation;
								},
								reservationDate: function () {
									return $scope.currentReservationDate;
								},
								eventId: function () {
									return $scope.currentEvent.id;
								},
								table: {}
							}
						});

						modalInstance.result.then(
							function () {},
							function () {
								ReservationService.getReservations($state.params.id, $scope.currentReservationDate, $scope.currentEvent.id).then(
									function successCallback(response) {
										sorting(response.guestlist, 'guest');
										sorting(response.pending, 'pending');
										sorting(response.approved, 'approved');
									},
									function errorCallback() {
										Toastr.error('Reservations loading failed', 'Error');
									}
								);
							}
						);
					},
					function errorCallback() {
						Toastr.error('Reservation loading failed', 'Error');
					}
				);
			}
		};

		$scope.open = function($event) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.opened = true;
		};

		$scope.changeDate = function (next) {
			if (next) {
				VenueService.getNextEventDate({
					id: $state.params.id,
					from: UtilService.formatRequestDate($scope.date)
				}).then(
					function (response) {
						$scope.date = new Date(response.date);
						$scope.getEventsList();
					},
					function () {
						Toastr.error('Next date loading error', 'Error');
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
					function () {
						Toastr.error('Previous date loading error', 'Error');
					}
				);
			}
		};

		var sorting = function (array, type) {
			var notDeletedReservations = [];
			var deletedReservations = [];
			angular.forEach(array, function (el) {
				if (el.deleted === true) {
					deletedReservations.push(el);
				} else {
					notDeletedReservations.push(el);
				}
			});

			notDeletedReservations = sortReservationById(notDeletedReservations);
			deletedReservations = sortReservationById(deletedReservations);

			if (type === 'guest') {
				$scope.guestList = notDeletedReservations;
				$scope.guestListDeleted = deletedReservations;
			}
			if (type === 'pending') {
				$scope.pendingList = notDeletedReservations;
				$scope.pendingListDeleted = deletedReservations;
			}
			if (type === 'approved') {
				$scope.approvedList = notDeletedReservations;
				$scope.approvedListDeleted = deletedReservations;
			}
		};

		var sortReservationById = function (array) {
			array.sort(function(a, b){
				var nameA=a.id, nameB=b.id;
				if (nameA < nameB) return 1;
				if (nameA > nameB) return -1;
				return 0;
			});
			return array;
		};

		$scope.viewFeedbacks = function (event, reservation) {
			console.log(angular.isFunction(event.stopPropagation));
			event.stopPropagation();
			$state.go('feedbacks', {id: $state.params.id, reservationId: reservation.id, guestInfo: reservation.guestInfo});
		};

		$scope.addNote = function (permission, reservation) {
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

			modalInstance.result.then(
				function () {
					modalCallback();
				},
				function () {
					modalCallback();
				}
			);
		};

		var modalCallback = function () {
			$scope.spinner = false;
			ReservationService.getReservations($state.params.id, $scope.currentReservationDate, $scope.currentEvent.id).then(
				function successCallback(response) {
					sorting(response.approved, 'approved');
					$scope.spinner = true;
				},
				function errorCallback() {
					$scope.spinner = true;
					Toastr.error('Reservations loading failed', 'Error');
				}
			);
		};

		$scope.checkOneCR = function (reservation, compliment) {
			if (compliment) {
				return reservation.complimentAll || reservation.complimentGirls || reservation.complimentGirlsQty || reservation.complimentGuys || reservation.complimentGuysQty;
			} else {
				return reservation.reducedAll || reservation.reducedGirls || reservation.reducedGirlsQty || reservation.reducedGuys || reservation.reducedGuysQty;
			}
		};
	}]);
}());
