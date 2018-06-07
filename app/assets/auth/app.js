(function () {
	'use strict';

	angular.module('mrblack.auth', [
		'ui.router',
		'ngAnimate',
		'ui.mask',
		'ngResource'
	])
		.config(function ($urlRouterProvider, $locationProvider, $httpProvider, $stateProvider) {
			$locationProvider.html5Mode({
				enabled: true,
				requireBase: false
			}).hashPrefix("!");

			$stateProvider
					.state('signup', {
						url: '/signup',
						controller: 'SignUpController',
						templateUrl: 'assets/auth/modules/signup/signup.html'
					})
					.state('signin', {
						url: '/signin',
						controller: 'SignInController',
						templateUrl: 'assets/auth/modules/signin/signin.html'
					})
					.state('confirm', {
						url: '/confirm',
						controller: 'ConfirmController',
						templateUrl: 'assets/auth/modules/confirm/confirm.html'
					});
		})
		.run(function ($rootScope, $state, $location, $window) {
		});
}());