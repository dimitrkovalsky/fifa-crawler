fifaApp.controller('SquadController', function ($rootScope, $scope, Squads, SquadsBuy,
                                                SquadsBuyAll, MinPrice, Tags) {


    $scope.onLoaded = function (result) {
        $scope.squad = result;
        $scope.loaded = true;
        $scope.isLoading = false;
        $scope.loadAllSquads();
    };

    $scope.loadSquad = function () {
        $scope.isLoading = true;
        Squads.get({id: $scope.squadId}, $scope.onLoaded, $rootScope.onError);
    };

    $scope.loadAllSquads = function () {
        Squads.query({}, function (response) {
            $scope.squads = response;
        }, $rootScope.onError);
    };

    $scope.loadStoredSquad = function (id) {
        $scope.squadId = id;
        $scope.loadSquad();
    };

    $scope.removeSquad = function(id) {
        Squads.delete({id: id}, $scope.loadAllSquads, $rootScope.onError);
    }

    $scope.reloadSquad = function () {
        $scope.isLoading = true;
        Squads.save({squadId: $scope.squadId}, $scope.onLoaded, $rootScope.onError);
    };


    $scope.buyAll = function () {
        $scope.isLoading = true;
        var players = [];
        angular.forEach($scope.squad.players, function (value, key) {
            if (!value.inClub) {
                players.push($scope.buildRequest(value));
            }
        });
        SquadsBuyAll.save({players: players}, $scope.loadSquad, $rootScope.onError)
    };

    $scope.buySingle = function (player) {
        $scope.isLoading = true;
        SquadsBuy.save($scope.buildRequest(player), $scope.loadSquad, $rootScope.onError);
    };

    $scope.reloadPrice = function (player) {
        $scope.isLoading = true;
        MinPrice.save({id: player.playerId}, $scope.loadSquad, $rootScope.onError);
    };

    $scope.buildRequest = function (player) {
        return {
            playerId: player.playerId,
            maxPrice: player.minPrice.price,
            playerName: player.profile.name
        };
    };


    $scope.tagMissed = function(player) {
       return player.tradeStatus.tags.indexOf("SBC") <= -1
    };

    $scope.addTag = function(player) {
         Tags.save({
             playerId: player.playerId,
             tag: "SBC"
         }, function(){
            player.tradeStatus.tags.push("SBC")
         }, $rootScope.onError);
    };

    $scope.loadAllSquads();
});