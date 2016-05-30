fifaApp.controller('LiveController', function ($scope, Monitoring) {
    $scope.stompClient = null;
    $scope.monitoringResults = [];

    $scope.setConnected = function (connected) {
        $scope.connected = connected;
    };

    $scope.connect = function () {
        var socket = new SockJS('/updates');
        $scope.stompClient = Stomp.over(socket);
        $scope.stompClient.connect({}, function (frame) {
            $scope.setConnected(true);
            console.log('Connected: ' + frame);
            $scope.stompClient.subscribe('/topic/live', $scope.onUpdate);
        });
    };

    $scope.onUpdate = function (frame) {
        debugger;
        var object =  JSON.parse(frame.body);
        var player = $scope.parsePlayer(object);
        $scope.updateRow(player);
    };

    $scope.disconnect = function () {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        $scope.setConnected(false);
        console.log("Disconnected");
    };

    $scope.onSuccess = function(result){
       $scope.monitoringResults = [];
       angular.forEach(result, function(value, key) {
          $scope.monitoringResults.push($scope.parsePlayer(value));
       });
    };

    $scope.parsePlayer = function(value) {
       var currentPrice = value.history.currentPrice.price.pc.price;
       var lastPrice = value.history.lastPrice.price.pc.price;
       var lastUpdate = value.history.lastPrice.price.pc.lastUpdate;
       var firstPrice = value.history.firstPrice.price.pc.price;
       var up = currentPrice > lastUpdate;
       return {
          id:value.id,
          name:value.name,
          currentPrice:currentPrice,
          lastPrice:lastPrice,
          firstPrice:firstPrice,
          lastUpdate:lastUpdate,
          image:value.image,
          up:up
       };
    };

     $scope.updateRow = function(player) {
         for(var i in $scope.monitoringResults){
            if($scope.monitoringResults[i].id == player.id){
               $scope.monitoringResults[i] = player;
               $scope.highlightRow(player.id);
               return;
            }
         }
         $scope.monitoringResults.push(player);
         $scope.highlightRow(player.id);
     };

    $scope.highlightRow = function(id) {
      var selector ='#player-row-' + id;
      $(selector).css("background-color", "red");
      setTimeout(function() {
          $(selector).css("background-color", "white");
      }, 2000);
    };

    $scope.onError = function(error){
          console.log(error);
     };

    $scope.updateMonitor = function() {
       console.log("Updating monitor...");
       Monitoring.query({},$scope.onSuccess, $scope.onError);
    };

    $scope.updateMonitor();
    $scope.connect();

     $scope.$on("$destroy", function() {
         $scope.disconnect();
     });
});