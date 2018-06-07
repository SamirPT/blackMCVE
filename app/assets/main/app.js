(function () {
  'use strict';

  angular.module('mrblack.main', [
      'ui.router',
      'ngResource',
      'ui.mask',
      'ui.bootstrap',
      'ngCropper',
      'ngFileUpload',
      'ui.select',
      'ngSanitize',
      'easypiechart',
      'angular-click-outside',
      'angularMoment'
    ])
    .config(function ($urlRouterProvider, $locationProvider, $httpProvider, $stateProvider, $resourceProvider) {
        $locationProvider.html5Mode({
            enabled: true,
            requireBase: false
        }).hashPrefix("!");
        $resourceProvider.defaults.cancellable = true;
        $urlRouterProvider.otherwise('/my_venues');
        $stateProvider
            .state('account', {
                url: '/account',
                controller: 'AccountCtrl',
                templateUrl: '/assets/main/modules/account/content.html'
            })
            .state('my_venues', {
                url: '/my_venues',
                controller: 'MyVenuesCtrl',
                templateUrl: '/assets/main/modules/venues/my_venues/my_venues.html'
            })
            .state('venues', {
                url: '/venues',
                controller: 'VenuesCtrl',
                templateUrl: '/assets/main/modules/venues/venues/venues.html'
            })
            .state('my_preferred_roles', {
                url: '/my_preferred_roles',
                controller: 'DetailedVenueCtrl',
                templateUrl: '/assets/main/modules/venues/my_preferred_roles/my_preferred_roles.html',
                params: {
                    venue: ''
                }
            })
            .state('clicker', {
                url: '/venue/:id/clicker',
                controller: 'ClickerCtrl',
                templateUrl: '/assets/main/modules/menu/clicker/clicker.html'
            })
            .state('clicker_menu', {
                url: '/venue/:id/clicker_menu',
                controller: 'ClickerCtrl',
                templateUrl: '/assets/main/modules/menu/clicker/clicker.menu.html'
            })
            .state('employees', {
                url: '/venue/:id/employees',
                controller: 'EmployeesCtrl',
                templateUrl: '/assets/main/modules/menu/employees/employees.html'
            })
            .state('events', {
                url: '/venue/:id/events',
                controller: 'EventsCtrl',
                templateUrl: '/assets/main/modules/menu/events/events.html'
            })
            .state('promotions', {
                url: '/venue/:id/promotions',
                controller: 'PromotionsCtrl',
                templateUrl: '/assets/main/modules/menu/promotions/promotions.html'
            })
            .state('reservations', {
                url: '/venue/:id/reservations',
                controller: 'ReservationsCtrl',
                templateUrl: '/assets/main/modules/menu/reservations/reservations.html',
                params: {
                    tab: ''
                }
            })
            .state('schedule', {
                url: '/venue/:id/schedule',
                controller: 'ScheduleCtrl',
                templateUrl: '/assets/main/modules/menu/schedule/schedule.html'
            })
            .state('tables', {
                url: '/venue/:id/tables',
                controller: 'TablesCtrl',
                templateUrl: '/assets/main/modules/menu/tables/tables.html'
            })
            .state('reports', {
                url: '/venue/:id/reports',
                controller: 'ReportsCtrl',
                templateUrl: '/assets/main/modules/menu/reports/reports.html'
            })
            .state('feedbacks', {
                url: '/venue/:id/feedbacks',
                controller: 'FeedbacksCtrl',
                templateUrl: '/assets/main/modules/menu/feedbacks/feedbacks.html',
                params: {
                    userId: '',
                    reservationId: '',
                    guestInfo: ''
                }
            })
            .state('reports_clicker', {
                url: '/venue/:id/reports_clicker',
                controller: 'ReportsClickerCtrl',
                templateUrl: '/assets/main/modules/menu/reports/clicker/clicker_report.html'
            })
            .state('reports_reservations', {
                url: '/venue/:id/reports_reservations',
                controller: 'ReportsReservationCtrl',
                templateUrl: '/assets/main/modules/menu/reports/reservations/reservations_report.html'
            })
            .state('reports_promoter', {
                url: '/venue/:id/reports_promoter',
                controller: 'ReportsPromoterCtrl',
                templateUrl: '/assets/main/modules/menu/reports/promoters/promoters_report.html'
            })
            .state('reports_employee', {
                url: '/venue/:id/reports_employee',
                controller: 'ReportsEmployeeCtrl',
                templateUrl: '/assets/main/modules/menu/reports/employees/employees_report.html'
            })
            .state('employee_roles', {
                url: '/venue/:id/employee_roles',
                controller: 'EmployeesCtrl',
                templateUrl: '/assets/main/modules/menu/employees/employee.roles.html',
                params: {
                    user: ''
                }
            })
            .state('tags', {
                url: '/venue/:id/tags',
                controller: 'TagsCtrl',
                templateUrl: '/assets/main/modules/menu/tags/tags.html'
            })
            .state('customers', {
                url: '/venue/:id/customers',
                controller: 'CustomersCtrl',
                templateUrl: '/assets/main/modules/menu/customers/customers.html'
            })
            .state('venue_snapshot', {
                url: '/venue/:id/venue_snapshot',
                controller: 'VenueSnapshotCtrl',
                templateUrl: '/assets/main/modules/venues/venue_snapshot/venueSnapshot.html'
            })
            .state('associated', {
                url: '/venue/:id/associated',
                controller: 'AssociatedCtrl',
                templateUrl: '/assets/main/modules/menu/associated/associated.html'
            })
            .state('eod', {
                url: '/venue/:id/eod',
                controller: 'EODCtrl',
                templateUrl: '/assets/main/modules/menu/eod/eod.html'
            });
    })
    .run(function ($rootScope, $state, $location, $window, DictService) {
        DictService.loadRoles();
    });
}());
