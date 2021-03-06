fifaApp.controller('MarketController', function ($rootScope, $scope, NgTableParams, MarketInfo,
                                                 PlayerAutoBuy, AutoBuy, AutoBuyPlayer, MinPrice,
                                                  Tags) {
    $scope.tradeInfo = {};
    $scope.tradeInfo.maxPurchases = 10;
    $scope.filterTags = [];

    $scope.onPlayersLoaded = function (result) {
        $scope.players = [];
        angular.forEach(result, function(value, key){
            var player = value.tradeStatus;
            if(value.profile) {
                if(value.profile.color){
                    player.color = value.profile.color;
                }
                $scope.players.push(player);
            }
        });
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
        var queryString = $scope.filterTags.join();
        PlayerAutoBuy.query({tags: queryString}, $scope.onPlayersLoaded, $rootScope.onError);
    };

    $scope.onInfoLoad = function (result) {
        $scope.marketInfo = result;
        $scope.autoBuyEnabled = result.autoBuyEnabled;
        $scope.robotEnabled = result.robotEnabled;
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
        var data = {};
        data.enabled = enabled;
        if(enabled){
          data.purchases =  $scope.tradeInfo.maxPurchases;
        }
        AutoBuy.save(data, function() {
            $scope.updateInfo()
        }, $rootScope.onError);

    };

    $scope.enableRobot = function (enabled) {
        var data = {};
        data.robotEnabled = enabled;

        AutoBuy.save(data, $scope.updateInfo, $rootScope.onError);
    };

    $scope.onAllUpdated = function () {
        $scope.isLoading = false;
    };

    $scope.updateAllPrices = function () {
        $scope.isLoading = true;
        MinPrice.save({id: -1}, $scope.onAllUpdated, $rootScope.onError);
    };

    $scope.filterByTag = function (tag) {
        if(!$scope.isActiveTag(tag)){
            $scope.filterTags.push(tag);
            $scope.getAllPlayers();
        }
    };

    $scope.removeTag = function(tag) {
       var index = $scope.filterTags.indexOf(tag);
       if (index > -1) {
           $scope.filterTags.splice(index, 1);
       }

       $scope.getAllPlayers();
    }

    $scope.isActiveTag = function(tag) {
       return $scope.filterTags.indexOf(tag) >= 0
    };

   $scope.removePlayerTag = function (tag, playerId) {
       Tags.delete({
           playerId: playerId,
           tag: tag
       }, $scope.getAllPlayers, $rootScope.onError);
   };

    $scope.updateInfo();
    $scope.getAllPlayers();
});