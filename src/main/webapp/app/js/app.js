var fifaApp = angular.module('fifaApp', ['ngResource', 'ui.router', 'ui.bootstrap',
    'fifa-directives']);

fifaApp.factory('Monitoring', function ($resource) {
    return $resource('/api/monitoring/:id');
});

fifaApp.factory('Players', function ($resource) {
    return $resource('/api/players/:id');
});

fifaApp.factory('PlayersFiltered', function ($resource) {
    return $resource('/api/players/source/:source');
});

fifaApp.factory('Sources', function ($resource) {
    return $resource('/api/players/sources/');
});

fifaApp.factory('ManageResource', function ($resource) {
    return $resource('/api/manage/fetch/:id');
});

fifaApp.factory('Tracking', function ($resource) {
    return $resource('/api/manage/track/:id');
});

fifaApp.factory('StatisticResource', function ($resource) {
    return $resource('/api/stats/');
});

fifaApp.config(function ($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise("/index");

    $stateProvider.state('manage', {
            url: '/manage',
            templateUrl: 'manage.html',
            controller: 'ManageController'
        })
        .state('live', {
            url: '/live',
            templateUrl: 'live.html',
            controller: 'LiveController'
        })
        .state('single-player', {
            url: '/player/:id',
            templateUrl: 'player.html',
            controller: 'SinglePlayerController'
        })
        .state('players', {
            url: '/players',
            templateUrl: 'players.html',
            controller: 'PlayerController'
        });
});