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


})();