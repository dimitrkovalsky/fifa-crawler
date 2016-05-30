fifaApp.controller('SinglePlayerController', function ($rootScope, $scope, $stateParams, Players) {
    $scope.id = $stateParams.id;


    $scope.onLoaded = function (player) {
        console.log(player);
        $scope.profile = player.profile;
        $scope.history = player.history;
        $scope.draw($scope.history)
    };

    $scope.getLastName = function () {
        if ($scope.profile) {
            var n = $scope.profile.info.name.split(" ");
            return n[n.length - 1].toUpperCase();
        } else
            return "Undefined";
    };

    $scope.draw = function (history) {
        if (!$scope.chartLoaded()) {
            setTimeout(function () {
                $scope.draw(history);
            }, 1000);
        } else {
            drawChart();
        }
        function drawChart() {
            function getPrice(record) {
                if (record && record.price && record.price.pc && record.price.pc.price)
                    return record.price.pc.price;
            }

            var data = new google.visualization.DataTable();
            data.addColumn('date', 'Day');
            data.addColumn('number', 'Price');
            var points = [];
            for (var i = 0; i < history.history.length; i++) {
                var price = getPrice(history.history[i]);
                if (price) {
                    var recorded = history.history[i].recoded;
                    points.push([new Date(recorded), price]);
                }
            }
            var currentPrice = getPrice(history.currentPrice);
            if (currentPrice) {
                var recordedDate = history.currentPrice.recoded;
                points.push([new Date(recordedDate), currentPrice]);
            } else {
                points.push([new Date(), 0]);
            }
            data.addRows(points);
            var subtitle = currentPrice || "None history";
            var options = {
                chart: {
                    title: 'Current price',
                    subtitle: subtitle
                },
                width: 900,
                height: 400
            };

            var chart = new google.charts.Line(document.getElementById('price-chart'));

            chart.draw(data, options);
        }


    };

    $scope.chartLoaded = function () {
        return !((typeof google === 'undefined') || (typeof google.visualization === 'undefined'));
    };

    Players.get({id: $scope.id}, $scope.onLoaded, $rootScope.onError);
})
;