fifaApp.controller('MonitoringController', function($scope, $interval, Monitoring){
    $scope.onSuccess = function(result){
       $scope.monitoringResults = [];
       angular.forEach(result, function(value, key) {
          var currentPrice = value.history.currentPrice.price.pc.price;
          var lastPrice = value.history.lastPrice.price.pc.price;
          var lastUpdate = value.history.lastPrice.price.pc.lastUpdate;
          var firstPrice = value.history.firstPrice.price.pc.price;
          var up = currentPrice > lastUpdate;
         $scope.monitoringResults.push({
            id:value.id,
            name:value.name,
            currentPrice:currentPrice,
            lastPrice:lastPrice,
            firstPrice:firstPrice,
            lastUpdate:lastUpdate,
            up:up
         });
       });
    };

    $scope.onError = function(error){
          console.log(error);
     };

    $scope.updateMonitor = function() {
        console.log("Updating monitor...")
       Monitoring.query({},$scope.onSuccess, $scope.onError);
    };

    $interval($scope.updateMonitor, 1000);

});