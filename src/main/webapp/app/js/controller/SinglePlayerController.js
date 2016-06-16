fifaApp.controller('SinglePlayerController', function ($rootScope, $scope, $stateParams,
PlayerAutoBuy, MinPrice) {
    $scope.id = $stateParams.id;


    $scope.onLoaded = function (player) {
        $scope.player = player;
    };

    $scope.onStatsLoaded = function (result) {
        $scope.playerPrice = result;
        $scope.draw($scope.playerPrice.prices);
    };

    $scope.getLastName = function () {
        if ($scope.profile) {
            var n = $scope.profile.info.name.split(" ");
            return n[n.length - 1].toUpperCase();
        } else
            return "Undefined";
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
           angular.forEach(prices, function (value, key) {
              matrix.push([value.price, value.amount]);
           });

           var data = new google.visualization.arrayToDataTable(matrix);
            var subtitle = "Price distribution";
            var options = {
                chart: {
                    title: 'Current price',
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

    $scope.updatePrice = function() {
      MinPrice.save({id: $scope.id}, $scope.onStatsLoaded, $rootScope.onError);
    }

    PlayerAutoBuy.get({id: $scope.id}, $scope.onLoaded, $rootScope.onError);

    MinPrice.get({id: $scope.id}, $scope.onStatsLoaded, $rootScope.onError);
});