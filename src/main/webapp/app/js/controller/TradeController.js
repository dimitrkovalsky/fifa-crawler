fifaApp.controller('TradeController', function ($rootScope, $scope, Trade) {


    $scope.onLoaded = function (result) {
        $rootScope.trades = result;
        angular.forEach($rootScope.trades, function(value, key){
            if(value.profile){
                value.color = value.profile.color;
            }
            value.id = value.playerId;
        });
    };

    Trade.query({}, $scope.onLoaded, $rootScope.onError);
    $rootScope.updateTradepile();
});