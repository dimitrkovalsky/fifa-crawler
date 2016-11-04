fifaApp.controller('SquadController', function ($rootScope, $scope, Squads) {


    $scope.onLoaded = function (result) {
        $scope.squad = result;
        $scope.loaded = true;
        $scope.loadAllSquads();
    };

    $scope.loadSquad = function () {
        Squads.get({id:$scope.squadId}, $scope.onLoaded, $rootScope.onError);
    };

    $scope.loadAllSquads = function () {
        Squads.query({}, function(response) {
            $scope.squads = response;
        }, $rootScope.onError);
    };

    $scope.loadStoredSquad = function (id) {
       $scope.squadId = id;
       $scope.loadSquad();
    };

    $scope.reloadSquad = function () {
       Squads.save({squadId:$scope.squadId},$scope.onLoaded, $rootScope.onError);
    };

    $scope.loadAllSquads();
});