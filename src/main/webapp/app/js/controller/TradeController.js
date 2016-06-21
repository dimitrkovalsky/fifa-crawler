fifaApp.controller('TradeController', function ($rootScope, $scope, Trade) {

    $scope.onLoaded = function (result) {
        $scope.trades = result;
    };


    Trade.query({}, $scope.onLoaded, $rootScope.onError);
});