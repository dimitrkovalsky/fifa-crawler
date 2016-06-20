fifaApp.controller('MarketController', function ($rootScope, $scope, NgTableParams, MarketInfo,
                                                 PlayerAutoBuy, AutoBuy, AutoBuyPlayer, MinPrice) {

    $scope.onPlayersLoaded = function (result) {
        $scope.players = result;
        angular.forEach($scope.players, function (value, key) {
            if (value.minMarketPrice)
                value.diff = value.minMarketPrice - value.maxPrice;
            else {
                value.minMarketPrice = 0;
                value.diff = 0;
            }
        });
        $scope.tableParams = new NgTableParams({
            page: 1,
            count: 100,
            sorting: {name: "asc"}
        }, {
            dataset: $scope.players
        });
        $rootScope.updateStats();
    };

    $scope.getPlayerById = function (id) {
        var player = undefined;
        angular.forEach($scope.players, function (value, key) {
            if (value.id == id)
                player = value;
        });
        return player;
    };
    $scope.onError = function (error) {
        console.log(error);
    };

    $scope.getAllPlayers = function () {
        console.log("Getting all players...");
        PlayerAutoBuy.query({}, $scope.onPlayersLoaded, $rootScope.onError);
    };

    $scope.onInfoLoad = function (result) {
        $scope.marketInfo = result;
        $scope.autoBuyEnabled = result.autoBuyEnabled;
        $rootScope.updateStats();
    };

    $scope.changeAutoBuyStatus = function (id, enabled) {
        AutoBuyPlayer.save({}, {
            id: id, enabled: enabled
        }, function () {
            $scope.getPlayerById(id).enabled = enabled;
        }, $rootScope.onError);
    };

    $scope.updateInfo = function () {
        MarketInfo.get({}, $scope.onInfoLoad, $rootScope.onError);
    };

    $scope.enableAutoBuy = function (enabled) {
        AutoBuy.save({enabled: enabled}, $scope.updateInfo, $rootScope.onError);
    };

    $scope.onAllUpdated = function () {
        $scope.isLoading = false;
    };

    $scope.updateAllPrices = function () {
        $scope.isLoading = true;
        MinPrice.save({id: -1}, $scope.onAllUpdated, $rootScope.onError);
    };

    $scope.updateInfo();
    $scope.getAllPlayers();
});