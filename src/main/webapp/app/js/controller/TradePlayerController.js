fifaApp.controller('TradePlayerController', function ($controller, $rootScope, $scope, $stateParams, Trade, MinPrice,
                                                      PlayerAutoBuy) {
    angular.extend(this, $controller('PlayerController', {
        $scope: $scope,
        $rootScope: $rootScope,
        $stateParams: $stateParams,
        PlayerAutoBuy: PlayerAutoBuy,
        MinPrice: MinPrice
    }));

    $scope.id = $stateParams.id;

    $scope.onStatsLoaded = function (result) {
        $scope.isLoading = false;
        $scope.playerPrice = result;
        $scope.draw($scope.playerPrice.prices);
    };

    $scope.updatePrice = function () {
        $scope.isLoading = true;
        MinPrice.save({id: $scope.id}, $scope.onStatsLoaded, $rootScope.onError);
    };

    function getUnassigned() {
        $scope.onLoaded = function (result) {
            $rootScope.trades = result;
            $scope.filterPlayers($scope.id);
        };

        Trade.query({}, $scope.onLoaded, $rootScope.onError);
    }

    $scope.filterPlayers = function (id) {
        $scope.toSell = [];
        if (!$rootScope.trades) {
            getUnassigned();
            return
        }
        for (var index in $rootScope.trades) {
            if ($rootScope.trades[index].playerId == id) {
                $scope.toSell = $rootScope.trades[index].items;
                $scope.tradeStatus = $rootScope.trades[index].tradeStatus;

            }
        }
        $scope.defineSellPrice();
    };

    $scope.defineSellPrice = function () {
        var startPrice = {};
        if ($scope.tradeStatus.sellStartPrice && $scope.tradeStatus.sellBuyNowPrice) {
            startPrice.sellStartPrice = $scope.tradeStatus.sellStartPrice;
            startPrice.sellBuyNowPrice = $scope.tradeStatus.sellBuyNowPrice;
        } else {
            startPrice.sellStartPrice = $scope.tradeStatus.maxPrice + 200;
            startPrice.sellBuyNowPrice = $scope.tradeStatus.maxPrice + 400;
        }
        $scope.sellPrice = startPrice;
    };

    $scope.sell = function (tradeId) {
        Trade.save({
            itemId: tradeId,
            startPrice: $scope.sellPrice.sellStartPrice,
            buyNow: $scope.sellPrice.sellBuyNowPrice
        }, $scope.init, $rootScope.onError);
    };

    $scope.init = function () {
        $scope.filterPlayers($scope.id);

        MinPrice.get({id: $scope.id}, $scope.onStatsLoaded, $rootScope.onError);
    };

    $scope.init();
});