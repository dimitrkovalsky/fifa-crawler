fifaApp.controller('PlayerController', function ($rootScope, $scope, $stateParams, Tags,
                                                 PlayerAutoBuy, MinPrice, PlayerUpdate, AutoBuyPlayer) {
    $scope.id = $stateParams.id;
    $scope.selectedTag = "";

    $scope.onLoaded = function (result) {
        $scope.player = result.tradeStatus;
        $scope.profile = result.profile;
        if ($scope.player == null) {
            $scope.isNew = true;
            $scope.player = {};
            $scope.player.maxPrice = 1000;
            $scope.player.name = $scope.profile.name;
        } else {
            $scope.isNew = false;
        }
    };

    $scope.onStatsLoaded = function (result) {
        $scope.isLoading = false;
        $scope.playerPrice = result;
        $scope.draw($scope.playerPrice.prices);
        $scope.getPlayerInfo();
    };


    $scope.addToMonitoring = function () {
        var id = $scope.profile.id;
        PlayerUpdate.save({id: id, maxPrice: 1000}, $scope.getPlayerInfo, $rootScope.onError);
    };

    $scope.draw = function (prices) {
        if (!$scope.chartLoaded()) {
            setTimeout(function () {
                $scope.draw(prices);
            }, 1000);
        } else {
            drawChart();
        }
        function drawChart() {
            var matrix = [];
            matrix.push(["Price", "Amount"]);
            var counter = 0;
            angular.forEach(prices, function (value, key) {
                matrix.push([value.price, value.amount]);
                counter = counter + value.amount;
            });
            var title = counter + " players";
            var data = new google.visualization.arrayToDataTable(matrix);
            var subtitle = "Price distribution";

            var options = {
                chart: {
                    title: title,
                    subtitle: subtitle
                },
                width: 900,
                height: 400
            };

            var chart = new google.charts.Bar(document.getElementById('price-chart'));
            chart.draw(data, options);
        }
    };

    $scope.chartLoaded = function () {
        return !((typeof google === 'undefined') || (typeof google.visualization === 'undefined'));
    };

    $scope.updatePrice = function () {
        $scope.isLoading = true;
        MinPrice.save({id: $scope.id}, $scope.onStatsLoaded, $rootScope.onError);
    };

    $scope.updatePlayer = function () {
        PlayerUpdate.save({
                id: $scope.player.id,
                name: $scope.player.name,
                maxPrice: $scope.player.maxPrice
            },
            $scope.getPlayerInfo, $rootScope.onError);
    };

    $scope.getPlayerInfo = function () {
        PlayerAutoBuy.get({id: $scope.id}, $scope.onLoaded, $rootScope.onError);
    };

    $scope.changeAutoBuyStatus = function (id, enabled) {
        AutoBuyPlayer.save({}, {
            id: id, enabled: enabled
        }, function () {
            $scope.player.enabled = enabled;
        }, $rootScope.onError);
    };

    $scope.addTag = function () {
        Tags.save({
            playerId: $scope.player.id,
            tag: $scope.selectedTag
        }, $scope.getPlayerInfo, $rootScope.onError);
    };

    $scope.removeTag = function (tag) {
        Tags.delete({
            playerId: $scope.player.id,
            tag: tag
        }, $scope.getPlayerInfo, $rootScope.onError);
    };

    $scope.getAvailableTags = function () {
        var tags = [];
        angular.forEach($rootScope.tags, function (value, key) {
            if ($scope.player && $scope.player.tags && $scope.player.tags.indexOf(value) > -1) {
            } else {
                tags.push(value);
            }
        });
        return tags;
    };

    $scope.getPlayerInfo();

    MinPrice.get({id: $scope.id}, $scope.onStatsLoaded, $rootScope.onError);
});