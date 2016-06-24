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
        $scope.tradeStatus = {};
        if (!$rootScope.trades) {
            getUnassigned();
            return
        }
        for (var index in $rootScope.trades) {
            if ($rootScope.trades[index] && $rootScope.trades[index].playerId == id) {
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
            playerId: $scope.id,
            startPrice: $scope.sellPrice.sellStartPrice,
            buyNow: $scope.sellPrice.sellBuyNowPrice
        }, $scope.updateSell, $rootScope.onError);
    };

    $scope.sellAll = function () {
        for (var index in $scope.toSell) {
            if ($scope.toSell[index].id) {
                 $scope.sell($scope.toSell[index].id);
            }
        }
    };

    $scope.updateSell = function () {
        getUnassigned();

        $scope.getPlayerInfo();
        MinPrice.get({id: $scope.id}, $scope.onStatsLoaded, $rootScope.onError);
    };

    $scope.filterPlayers($scope.id);

    $scope.getPlayerInfo();
    MinPrice.get({id: $scope.id}, $scope.onStatsLoaded, $rootScope.onError);
});