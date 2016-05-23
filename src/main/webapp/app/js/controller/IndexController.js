fifaApp.controller('IndexController', function($rootScope, $scope, StatisticResource) {
    $rootScope.updateStats = function() {
        StatisticResource.get({}, function(stats){
            $scope.stats = stats
        }, function(){});
    };

    $rootScope.updateStats();
});