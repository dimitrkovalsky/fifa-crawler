fifaApp.controller('IndexController', function ($rootScope, $scope, StatisticResource) {
    $scope.stompClient = null;
    $rootScope.logs = [];
    $rootScope.autoScroll = true;

    $scope.logConfig = {
        autoHideScrollbar: false,
        theme: 'light',
        advanced:{
            updateOnContentResize: true
        },
        setHeight: 200,
        scrollInertia: 0
    };

    $rootScope.updateStats = function () {
        StatisticResource.get({}, function (stats) {
            $rootScope.stats = stats
        }, function () {
        });
    };

    $rootScope.onError = function (error) {
        console.log("Error ", error);
    };

    $scope.setConnected = function (connected) {
        $scope.connected = connected;
    };

    $scope.connect = function () {
        var socket = new SockJS('/updates');
        $scope.stompClient = Stomp.over(socket);
        $scope.stompClient.connect({}, function (frame) {
            $scope.setConnected(true);
            $scope.stompClient.subscribe('/topic/live', $scope.onUpdate);
        });
    };

    $scope.onUpdate = function (frame) {
        var msg = JSON.parse(frame.body);
        if ($rootScope.logs.length > 10) {
            $rootScope.logs.shift();
        }
        $rootScope.logs.push(msg);
        $rootScope.$apply();
        if ($rootScope.autoScroll) {
            var elem = document.getElementById('collapseLog');
            elem.scrollTop = elem.scrollHeight;
        }
    };

    $rootScope.onScroll = function(){
        $rootScope.autoScroll = false;
    };

    $scope.disconnect = function () {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        $scope.setConnected(false);
        console.log("Disconnected");
    };

    $rootScope.clearLogs = function () {
        $rootScope.logs.clear();
        $rootScope.$apply();
    };

    $scope.connect();
    $rootScope.updateStats();
});