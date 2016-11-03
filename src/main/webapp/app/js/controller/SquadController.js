fifaApp.controller('SquadController', function ($rootScope, $scope, Squads) {

    $scope.onLoaded = function (result) {
        $scope.squad = result;
        $scope.loaded = true;
    };

    $scope.loadSquad = function () {
        Squads.get({id:$scope.squadId}, $scope.onLoaded, $rootScope.onError);
    };

});