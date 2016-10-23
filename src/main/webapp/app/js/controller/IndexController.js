fifaApp.controller('IndexController', function ($rootScope, $scope, StatisticResource, Tradepile,
                                                Suggestions, LeagueResource, Tags) {
    $scope.stompClient = null;
    $rootScope.logs = [];
    $rootScope.autoScroll = true;
    $rootScope.tags = [];

    $rootScope.loadLeagues = function () {
        LeagueResource.get({}, function (res) {
            $rootScope.leaguesMap = res;
        }, $rootScope.onError);
    };

    $rootScope.loadTags = function () {
        Tags.query({}, function (result) {
            $rootScope.tags = [];
            angular.forEach(result, function (value, key) {
                $rootScope.tags.push(value.name);
            });
            console.log("Tags loaded : " + $rootScope.tags);
        }, $rootScope.onError);
    };

    $scope.logConfig = {
        autoHideScrollbar: false,
        theme: 'light',
        advanced: {
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

    $rootScope.toTime = function (time) {
        var minutes = "0" + Math.floor(time / 60);
        var seconds = "0" + (time - minutes * 60);
        return minutes.substr(-2) + ":" + seconds.substr(-2);
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
        switch (msg.messageType) {
            case 'log': {
                $rootScope.addLog(msg);
                break;
            }
            case 'bought' :
                $rootScope.updateTradepile(msg);
                break;
            default :
                console.log("Unrecognized message type : " + msg);
        }
    };

    $rootScope.addLog = function (msg) {
        $rootScope.logs.push(msg);
        $rootScope.$apply();
        if ($rootScope.autoScroll) {
            var elem = document.getElementById('collapseLog');
            elem.scrollTop = elem.scrollHeight;
        }
    };

    $rootScope.onUpdateTradepile = function (msg) {
        $rootScope.unassigned = msg.unassigned;
        $rootScope.canSell = msg.canSell;
        $rootScope.purchasesRemained = msg.purchasesRemained;
        if ($scope.stats) {
            $scope.stats.credits = msg.credits;
        }
        document.title = "Fifa (" + $rootScope.unassigned + ")";
        $rootScope.updateFavicon($rootScope.canSell);
    };

    $rootScope.favicon = new Favico({
        animation: 'slide',
        position: 'up',
        bgColor: '#5CB85C',
        textColor: '#ff0'
    });

    $rootScope.updateFavicon = function (badge) {
        $rootScope.favicon.badge(badge);
    };

    $rootScope.onScroll = function () {
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

    $rootScope.updateTradepile = function () {
        Tradepile.get({}, $rootScope.onUpdateTradepile, $rootScope.onError);
    };

    $rootScope.getLeagueName = function (id) {
        return $rootScope.leaguesMap[id].abbrName;
    };

    $scope.connect();
    $rootScope.updateStats();
    $rootScope.updateTradepile();
    $rootScope.loadLeagues();
    $rootScope.loadTags();
});