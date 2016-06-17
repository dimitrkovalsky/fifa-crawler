fifaApp.controller('MarketController', function ($rootScope, $scope, MarketInfo, PlayerAutoBuy,
                                                 AutoBuy) {
    $scope.onPlayersLoaded = function (result) {
        $scope.players = result;
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
    };

    $scope.changeAutoBuyStatus = function (id, enabled) {
        AutoBuy.update({id: id, enabled: enabled}, $scope.getAllPlayers(), $rootScope.onError)
    };

    $scope.getAllPlayers();
    MarketInfo.get({}, $scope.onInfoLoad, $rootScope.onError);
});