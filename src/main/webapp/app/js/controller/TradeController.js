fifaApp.controller('TradeController', function ($rootScope, $scope, Trade, MinPrice) {


    $scope.onLoaded = function (result) {
        $rootScope.trades = result;
        angular.forEach($rootScope.trades, function(value, key){
            if(value.profile){
                value.color = value.profile.color;
            }
            value.id = value.playerId;
        });
    };

     $scope.onAllUpdated = function () {
         $scope.isLoading = false;
     };

     $scope.updateAllPrices = function () {
         $scope.isLoading = true;
         MinPrice.save({id: -100}, $scope.onAllUpdated, $rootScope.onError);
     };

    Trade.query({}, $scope.onLoaded, $rootScope.onError);
    $rootScope.updateTradepile();
});