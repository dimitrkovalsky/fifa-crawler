fifaApp.controller('LiveController', function ($scope) {
    $scope.stompClient = null;

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

    $scope.onUpdate = function (data) {
        console.log(">>>>" + data);
    };

    $scope.disconnect = function () {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        $scope.setConnected(false);
        console.log("Disconnected");
    };

    $scope.sendName = function () {
        $scope.stompClient.send("/app/updates", {}, JSON.stringify({'name': "sample"}));
    };
    $scope.connect();
});