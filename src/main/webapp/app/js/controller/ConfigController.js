fifaApp.controller('ConfigController', function ($rootScope, $scope, ConfigResource, ActivateTag, Parameters,
                                                 DeactivateTag, PriceUpdate, BuyStrategy, SellStrategy) {
    $scope.tagDistribution = {};
    $scope.activeTags = [];

    $scope.onLoaded = function (result) {
        $scope.tagDistribution = result.tags;
        $scope.activeTags = result.activeTags;
    };

    $scope.isActiveTag = function (tag) {
        return $scope.activeTags.indexOf(tag) >= 0
    };

    $scope.enableTag = function (tag) {
        ActivateTag.save({string: tag}, $scope.loadParams, $rootScope.onError);
    };

    $scope.disableTag = function (tag) {
        DeactivateTag.save({string: tag}, $scope.loadParams, $rootScope.onError);
    };

    $scope.onParamsLoaded = function (result) {
        $scope.params = result;
    };

    $scope.saveParams = function () {
        Parameters.save($scope.params, function () {
            Parameters.get({}, $scope.onParamsLoaded, $rootScope.onError);
        }, $rootScope.onError);
    };

    $scope.loadParams = function () {
        ConfigResource.get({}, $scope.onLoaded, $rootScope.onError);
        Parameters.get({}, $scope.onParamsLoaded, $rootScope.onError);
        $rootScope.updateStats();
    };

    $scope.updatePrices = function () {
        PriceUpdate.save({}, function () {
        }, $rootScope.onError);
    };

    $scope.loadStrategies = function() {
        BuyStrategy.query(function(result) {
            $scope.buyStrategies = result;
        }, $rootScope.onError);
        SellStrategy.query(function() {
            $scope.sellStrategies = result;
        }, $rootScope.onError);
    }

    $scope.loadStrategies();
    $scope.loadParams();

});