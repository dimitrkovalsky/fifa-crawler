fifaApp.controller('SinglePlayerController', function ($rootScope, $scope, $stateParams, Players) {
    $scope.id = $stateParams.id;


    $scope.onLoaded = function (player) {
        console.log(player);
        $scope.profile = player.profile;
        $scope.history = player.history;
    };

    $scope.getLastName = function () {
        if ($scope.profile) {
            var n = $scope.profile.info.name.split(" ");
            return n[n.length - 1].toUpperCase();
        } else
            return "Undefined";
    };

    $scope.draw = function () {
        if (!$scope.chartLoaded()) {
            setTimeout(function () {
                $scope.draw();
            }, 1000);
        } else {
            drawChart();
        }
        function drawChart() {
            var data = new google.visualization.DataTable();
            data.addColumn('number', 'Day');

            data.addRows([
                [1],
                [24],
                [3],
                [45],
                [54],
                [6],
                [7],
                [86],
                [98],
                [10],
                [11],
                [12],
                [13],
                [14]
            ]);

            var options = {
                chart: {
                    title: 'Player price change',
                    subtitle: 'last update'
                },
                width: 900,
                height: 500,
                axes: {
                    x: {
                        0: {side: 'top'}
                    }
                }
            };

            var chart = new google.charts.Line(document.getElementById('price-chart'));

            chart.draw(data, options);
        }


    };

    $scope.chartLoaded = function () {
        return !((typeof google === 'undefined') || (typeof google.visualization === 'undefined'));
    };

    $scope.draw();

    Players.get({id: $scope.id}, $scope.onLoaded, $rootScope.onError);
})
;