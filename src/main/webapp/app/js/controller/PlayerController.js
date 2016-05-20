fifaApp.controller('PlayerController', function($scope, Monitoring, Players, PlayersFiltered, Sources){
    $scope.onSuccess = function(result){
       $scope.players = [];
       angular.forEach(result, function(value, key) {
          console.log(value);
         $scope.players.push({
            id:value.id,
            name:value.info.name,
            position:value.info.position,
            total:value.info.stats.total,
            source:value.info.source,
            league:value.info.leagueName,
         });
       });
    };

    $scope.onError = function(error){
          console.log(error);
     };

    $scope.getAllPlayers = function() {
        console.log("Getting all players...")
       Players.query({},$scope.onSuccess, $scope.onError);
    };

    $scope.getAllPlayers();
});