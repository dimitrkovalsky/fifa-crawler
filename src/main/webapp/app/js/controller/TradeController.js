fifaApp.controller('TradeController', function ($rootScope, $scope, Trade) {


    $scope.onLoaded = function (result) {
        $rootScope.trades = result;
    };

    Trade.query({}, $scope.onLoaded, $rootScope.onError);
});