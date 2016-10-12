fifaApp.controller('SearchController', function ($rootScope, $scope, Search) {
    $scope.search = "";
    $scope.profiles = [];

    $scope.onLoaded = function (result) {
        $scope.profiles = result;
        console.log(result);
    };

    $scope.performSearch = function () {
        Search.query({phrase:$scope.search}, $scope.onLoaded, $rootScope.onError);
    };

    $scope.performSearch();
});