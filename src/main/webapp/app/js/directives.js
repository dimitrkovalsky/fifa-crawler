(function () {
    var module = angular.module('fifa-directives', ['ui.bootstrap']);

    module.directive('player', function () {
        return {
            restrict: 'E',
            templateUrl: 'templates/player.html'
        }
    });

    module.directive('smallPlayer', function () {
        return {
            restrict: 'E',
            templateUrl: 'templates/small-player.html'
        }
    });

    module.directive('searchRow', function () {
        return {
            restrict: 'E',
            templateUrl: 'templates/search-row.html'
        }
    });

    module.directive('searchForm', function () {
        return {
            restrict: 'E',
            templateUrl: 'templates/search-form.html'
        }
    });

    module.directive('tradeStatus', function () {
        return {
            restrict: 'E',
            templateUrl: 'templates/trade-status.html'
        }
    });

    module.directive('bidStatus', function () {
        return {
            restrict: 'E',
            templateUrl: 'templates/bid-status.html'
        }
    });

    module.directive('contracts', function () {
        return {
            restrict: 'E',
            templateUrl: 'templates/contracts.html'
        }
    });


})();