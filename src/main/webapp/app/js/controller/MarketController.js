fifaApp.controller('MarketController', function ($rootScope, $scope, MarketInfo, PlayerAutoBuy,
MinPrice) {
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

    $scope.onInfoLoad = function(result) {
        $scope.marketInfo = result;
    };

    $scope.getAllPlayers();
    MarketInfo.get({}, $scope.onInfoLoad, $rootScope.onError);
});