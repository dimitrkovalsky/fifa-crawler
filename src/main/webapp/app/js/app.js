var fifaApp = angular.module('fifaApp', ['ngResource', 'ui.router', 'ui.bootstrap',
    'fifa-directives', 'ngTable', 'ui.toggle']);

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

fifaApp.factory('PlayerUpdate', function ($resource) {
    return $resource('/api/market/player/update');
});

fifaApp.factory('PlayerAutoBuy', function ($resource) {
    return $resource('/api/market/player/:id');
});

fifaApp.factory('Search', function ($resource) {
    return $resource('/api/market/search/');
});

fifaApp.factory('Tags', function ($resource) {
    return $resource('/api/tags/:tag');
});

fifaApp.factory('Trade', function ($resource) {
    return $resource('/api/trade/');
});

fifaApp.factory('MinerSell', function ($resource) {
    return $resource('/api/miner/sell/price/:playerId');
});

fifaApp.factory('Tradepile', function ($resource) {
    return $resource('/api/trade/tradepile');
});

fifaApp.factory('Bid', function ($resource) {
    return $resource('/api/trade/bid');
});

fifaApp.factory('Search', function ($resource) {
    return $resource('/api/profiles/search');
});

fifaApp.factory('MarketSearch', function ($resource) {
    return $resource('/api/search');
});

fifaApp.factory('MinPrice', function ($resource) {
    return $resource('/api/market/player/:id/min/');
});

fifaApp.factory('Squads', function ($resource) {
    return $resource('/api/squad/:id/', {id : '@id'});
});

fifaApp.factory('SquadsBuy', function ($resource) {
    return $resource('/api/squad/buy');
});


fifaApp.factory('SquadsBuyAll', function ($resource) {
    return $resource('/api/squad/buyall');
});

fifaApp.factory('Suggestions', function ($resource) {
    return $resource('/app/res/fifa-players.json',{},{
       getData: {method:'GET', isArray: false}
    });
});

fifaApp.factory('LeagueResource', function ($resource) {
    return $resource('/api/info/leagues');
});

fifaApp.factory('ConfigResource', function ($resource) {
    return $resource('/api/config');
});

fifaApp.factory('ActivateTag', function ($resource) {
    return $resource('/api/config/activate');
});

fifaApp.factory('DeactivateTag', function ($resource) {
    return $resource('/api/config/deactivate');
});

fifaApp.factory('PriceUpdate', function ($resource) {
    return $resource('/api/config/update');
});

fifaApp.factory('Parameters', function ($resource) {
    return $resource('/api/config/parameters');
});

fifaApp.factory('BuyStrategy', function ($resource) {
    return $resource('/api/config/strategy/buy');
});

fifaApp.factory('SellStrategy', function ($resource) {
    return $resource('/api/config/strategy/sell');
});


fifaApp.config(function ($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise("/market");

    $stateProvider.state('single-player', {
            url: '/player/:id',
            templateUrl: 'player.html',
            controller: 'PlayerController'
        })
        .state('market', {
            url: '/market',
            templateUrl: 'market.html',
            controller: 'MarketController'
        })
        .state('trade-player', {
            url: '/trade-player/:id',
            templateUrl: 'trade-player.html',
            controller: 'TradePlayerController'
        })
        .state('trade', {
            url: '/trade',
            templateUrl: 'trade.html',
            controller: 'TradeController'
        })
        .state('search', {
            url: '/search',
            templateUrl: 'search.html',
            controller: 'SearchController'
        })
        .state('market-search', {
            url: '/market-search',
            templateUrl: 'market-search.html',
            controller: 'MarketSearchController'
        })
        .state('squad', {
            url: '/squad',
            templateUrl: 'squad.html',
            controller: 'SquadController'
        })
        .state('config', {
            url: '/config',
            templateUrl: 'config.html',
            controller: 'ConfigController'
        });
});