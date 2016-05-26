fifaApp.controller('SinglePlayerController', function ($rootScope, $scope, $stateParams, Players) {
    $scope.id = $stateParams.id;


    $scope.onLoaded = function (player) {
        console.log(player) ;
        $scope.profile = player.profile;
        $scope.history = player.history;
    };


    Players.get({id: $scope.id}, $scope.onLoaded, $rootScope.onError);
});