fifaApp.controller('PlayerController', function ($rootScope, $scope, $stateParams,
                                                       PlayerAutoBuy, MinPrice) {
    $scope.id = $stateParams.id;

    $scope.onLoaded = function (player) {
        $scope.player = player;
    };

    $scope.onStatsLoaded = function (result) {
        $scope.isLoading = false;
        $scope.playerPrice = result;
        $scope.draw($scope.playerPrice.prices);
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
        PlayerAutoBuy.save({
                id: $scope.player.id,
                name: $scope.player.name,
                maxPrice: $scope.player.maxPrice
            },
            $scope.getPlayerInfo, $rootScope.onError);
    };

    $scope.getPlayerInfo = function () {
        PlayerAutoBuy.get({id: $scope.id}, $scope.onLoaded, $rootScope.onError);
    };

    $scope.getPlayerInfo();
    MinPrice.get({id: $scope.id}, $scope.onStatsLoaded, $rootScope.onError);
});