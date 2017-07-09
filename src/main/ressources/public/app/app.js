'use strict';

var okeanosApp = angular.module('okeanosApp', [
    // Dépendances du "module"
    'ngRoute', 'ngResource',
    'okeanosAppControllers'
]);

var okeanoAppUrl = 'https://localhost:8080/api/v1/';

/**
 * Configuration du module principal : routeApp
 */
okeanosApp.config(['$routeProvider', '$httpProvider',
    function ($routeProvider, $httpProvider) {

        // Système de routage
        $routeProvider
            .when('/userLogin', {
                templateUrl: 'partials/userLogin.html',
                controller: 'loginCtrl'
            })
            .when('/dashboard', {
                templateUrl: 'partials/dashboard.html',
                controller: 'adherentFormCtrl',
                authorized: true
            })
            .when('/userForm', {
                templateUrl: 'partials/userForm.html',
                controller: 'adherentFormCtrl',
                authorized: true
            })
            .when('/adherentInfo', {
                templateUrl: 'partials/adherent_info.html',
                controller: 'adherentInfoCtrl',
                authorized: true
            })
            .when('/usersList', {
                templateUrl: 'partials/usersList.html',
                controller: 'listUsrCtrl',
                authorized: true
            })
            .when('/accountList', {
                templateUrl: 'partials/accountList.html',
                controller: 'accountListCtrl',
                authorized: true
            })
            .when('/saison', {
                templateUrl: 'partials/saisonList.html',
                controller: 'saisonListCtrl',
                authorized: true
            })
            .when('/divingTraining', {
                templateUrl: 'partials/diving_training.html',
                controller: 'divingTrainingCtrl',
                authorized: true
            })
            .when('/hockeyTeam', {
                templateUrl: 'partials/hockey_team.html',
                controller: 'hockeyTeamCtrl',
                authorized: true
            })
            .when('/subscriptionType', {
                templateUrl: 'partials/subscriptionType.html',
                controller: 'subscriptionTypeCtrl',
                authorized: true
            })
            .when('/ffessmLicence', {
                templateUrl: 'partials/ffessm_licence.html',
                controller: 'ffessmLicenceCtrl',
                authorized: true
            })
            .otherwise({
                redirectTo: '/dashboard'
            });


        //register the interceptor as a service
        $httpProvider.interceptors.push(function ($location, securityService) {
            return {
                'response': function (response) {
                    if (response.config.url.startsWith(okeanoAppUrl)) {
                        //Do your custom processing here
                        // do something on success
                        //console.log('myHttpInterceptor response : ' + response.config.url);
                        securityService.setSecurity(response.headers('isLogin'), response.headers('isAdmin'), response.headers('curentAccountId'));
                        //console.log(securityService.getSecurity());
                    }

                    return response;
                },
                'responseError': function (rejection) {
                    console.log('responseError');
                    if (rejection.status === 401) {
                        $location.url('/dashboard');
                    }
                }
            };
        });

}]);

/*

okeanosApp.run(function ($rootScope, $location, securityService) {
    $rootScope.$on("$routeChangeStart", function (event, next, current) {
        if (next.$$route.authorized && !securityService.isConnected()) {
            $location.url("/userLogin");
        }
    });
});
*/

okeanosApp.run(function ($rootScope, $location, $window) {
    $rootScope.$on('$routeChangeSuccess', function () {
        //console.log($location);

        if ($location.protocol() !== 'https') {
            $window.location.href = $location.absUrl().replace('http', 'https');
        };
    });
});
