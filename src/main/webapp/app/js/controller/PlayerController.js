fifaApp.controller('PlayerController', function ($rootScope, $scope, Monitoring, Players,
PlayersFiltered, Sources) {
    var ALL = "all";
    var BY_SOURCE = "by_source";
    var BY_LEAGUE = "by_league";
    $scope.filter = ALL;

    $scope.filterSource = "Winter Update";
    $scope.filterLeague = "";

    $scope.playerFilter = function () {
        switch ($scope.filter) {
            case BY_SOURCE:
                $scope.getAllBySource();
                break;
            case BY_LEAGUE:
                $scope.getAllByLeague();
                break;
            default:
                $scope.getAllPlayers();
                break;
        }
    };

    $scope.onPlayersLoaded = function (result) {
        $scope.players = [];
        angular.forEach(result, function (value, key) {
            $scope.players.push({
                id: value.id,
                name: value.info.name,
                position: value.info.position,
                total: value.info.stats.total,
                source: value.info.source,
                league: value.info.leagueName,
                underMonitoring: value.underMonitoring,
                image: value.info.image
            });
        });
    };
    $scope.onError = function (error) {
        console.log(error);
    };
    $scope.getAllPlayers = function () {
        console.log("Getting all players...");
        Players.query({}, $scope.onPlayersLoaded, $scope.onError);
    };

    $scope.getAllBySource = function () {
        PlayersFiltered.query({source: $scope.filterSource}, $scope.onPlayersLoaded, $scope.onError);
    };

    $scope.filterBySource = function (source) {
        $scope.filter = BY_SOURCE;
        $scope.filterSource = source;
        $scope.playerFilter();
    };

    $scope.getAllByLeague = function (league) {
        $scope.filter = BY_LEAGUE;
        $scope.filterLeague = league;
        $scope.playerFilter();
    };

    $scope.addToMonitoring = function (id) {
        Monitoring.save(id);
        $rootScope.updateStats();
        $scope.playerFilter();
    };

    $scope.removeFromMonitoring = function (id) {
        Monitoring.remove({id: id});
        $rootScope.updateStats();
        $scope.playerFilter();
    };

    $scope.getAllSources = function () {
        Sources.query({}, function (res) {
            $scope.sources = res;
        }, $scope.onError);
    };

    $scope.enableAll = function (){
        angular.forEach($scope.players, function (value, key) {
            if(!value.underMonitoring)
                $scope.addToMonitoring(value.id);
        });
    };

    $scope.disableAll = function () {
       angular.forEach($scope.players, function (value, key) {
          if(value.underMonitoring)
            $scope.removeFromMonitoring(value.id);
       });
    };

    $scope.playerFilter();
    $scope.getAllSources();
});