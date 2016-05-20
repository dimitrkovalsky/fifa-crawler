(function(){
    var module = angular.module('fifa-directives', ['ui.bootstrap']);

     module.directive('player', function() {
            return {
                restrict:'E',
                templateUrl:'templates/player.html'
            }
        });

        module.directive('team', function() {
            return {
                restrict:'E',
                templateUrl:'templates/team.html'
            }
        });
})();