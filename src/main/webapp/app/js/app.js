var fifaApp = angular.module('fifaApp', ['ngResource', 'ui.router', 'ui.bootstrap',
'fifa-directives']);

fifaApp.factory('Monitoring', function($resource) {
    return $resource('/api/monitoring/');
});

fifaApp.factory('Players', function($resource) {
    return $resource('/api/players/:id');
});

fifaApp.factory('PlayersFiltered', function($resource) {
    return $resource('/api/players/source/:source');
});

fifaApp.factory('Sources', function($resource) {
    return $resource('/api/players/sources/');
});


fifaApp.config(function($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise("/index");

    $stateProvider.state('monitor',{
        url: '/monitor',
        templateUrl: 'monitor.html',
        controller: 'MonitoringController'
    })
    .state('manage',{
        url: '/manage',
        templateUrl: 'manage.html',
        controller: 'ManageController'
    })
    .state('players',{
        url: '/players',
        templateUrl: 'players.html',
        controller: 'PlayerController'
    });;
});