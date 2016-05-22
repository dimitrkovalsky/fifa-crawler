fifaApp.controller('ManageController', function ($scope, ManageResource) {

    $scope.fetchTots = function () {
        $scope.fetch("tots");
    };

    $scope.fetchTows = function () {
        $scope.fetch("tows");
    };

    $scope.fetchSources = function () {
        $scope.fetch("sources");
    };

    $scope.fetchPlayer = function (playerId) {
        ManageResource.get({id: playerId, force: true}, $scope.onSuccess, $scope.onError);
    };

    $scope.fetch = function (toFetch) {
        ManageResource.save(toFetch, $scope.onSuccess, $scope.onError);
    };

    $scope.onSuccess = function (result) {
        console.log("Success fetch : " + result);
    };

    $scope.onSuccess = function (err) {
        console.log("Error fetch: " + err);
    };
});