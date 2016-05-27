fifaApp.controller('SinglePlayerController', function ($rootScope, $scope, $stateParams, Players) {
    $scope.id = $stateParams.id;


    $scope.onLoaded = function (player) {
        console.log(player) ;
        $scope.profile = player.profile;
        $scope.history = player.history;
    };

    $scope.getLastName = function() {
       if($scope.profile){
            var n = $scope.profile.info.name.split(" ");
            return n[n.length - 1].toUpperCase();
       } else
       return "Undefined";
    };
    Players.get({id: $scope.id}, $scope.onLoaded, $rootScope.onError);
});