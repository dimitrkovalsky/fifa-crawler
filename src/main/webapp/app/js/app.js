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

fifaApp.factory('MarketInfo', function ($resource) {
    return $resource('/api/market/info/');
});

fifaApp.factory('AutoBuy', function ($resource) {
    return $resource('/api/market/autobuy/');
});

fifaApp.factory('AutoBuyPlayer', function ($resource) {
    return $resource('/api/market/autobuy/player/');
});

fifaApp.factory('PlayerAutoBuy', function ($resource) {
    return $resource('/api/market/player/:id');
});

fifaApp.factory('MinPrice', function ($resource) {
    return $resource('/api/market/player/:id/min/');
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
        .state('market', {
             url: '/market',
             templateUrl: 'market.html',
             controller: 'MarketController'
         })
        .state('players', {
            url: '/players',
            templateUrl: 'players.html',
            controller: 'PlayerController'
        });
});