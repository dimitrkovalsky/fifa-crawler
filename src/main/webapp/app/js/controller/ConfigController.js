fifaApp.controller('ConfigController', function ($rootScope, $scope, ConfigResource, ActivateTag,
    DeactivateTag, PriceUpdate) {
    $scope.tagDistribution = {};
    $scope.activeTags = [];

    $scope.onLoaded = function (result) {
        $scope.tagDistribution = result.tags;
        $scope.activeTags = result.activeTags;
    };

    $scope.isActiveTag = function(tag) {
       return $scope.activeTags.indexOf(tag) >= 0
    };

    $scope.enableTag = function(tag) {
        ActivateTag.save({string: tag}, $scope.loadTags, $rootScope.onError);
    };

    $scope.disableTag = function(tag) {
        DeactivateTag.save({string: tag}, $scope.loadTags, $rootScope.onError);
    };

    $scope.loadTags = function() {
        ConfigResource.get({}, $scope.onLoaded, $rootScope.onError);
        $rootScope.updateStats();
    };

    $scope.updatePrices = function() {
        PriceUpdate.save({}, function(){}, $rootScope.onError);
    };

    $scope.loadTags();

});