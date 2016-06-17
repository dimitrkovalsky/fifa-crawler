fifaApp.controller('MarketController', function ($rootScope, $scope, MarketInfo, PlayerAutoBuy,
                                                 AutoBuy, AutoBuyPlayer) {

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
        $scope.autoBuyEnabled = result.autoBuyEnabled;
    };

    $scope.changeAutoBuyStatus = function (id, enabled) {
        AutoBuyPlayer.save({}, {id: id, enabled: enabled}, $scope.getAllPlayers,$rootScope.onError);
    };

    $scope.updateInfo = function(){
        MarketInfo.get({}, $scope.onInfoLoad, $rootScope.onError);
    };

    $scope.enableAutoBuy = function(enabled){
        AutoBuy.save({enabled: enabled}, $scope.updateInfo, $rootScope.onError);
    };

    $scope.updateInfo();
    $scope.getAllPlayers();
});