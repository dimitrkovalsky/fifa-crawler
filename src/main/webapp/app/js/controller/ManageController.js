fifaApp.controller('ManageController', function ($scope, $interval, ManageResource, Tracking) {

    $scope.fetchTots = function () {
        $scope.totsStatus = "Started";
        $scope.fetch("tots");
    };

    $scope.fetchTows = function () {
        $scope.towsStatus = "Started";
        $scope.fetch("tows");
    };

    $scope.fetchSources = function () {
        $scope.fetch("sources");
    };

    $scope.fetchPlayer = function (playerId) {
        ManageResource.get({id: playerId, force: true}, $scope.onSuccess, $scope.onError);
    };

    $scope.fetch = function (toFetch) {
        ManageResource.save(toFetch, function (res) {
            $scope.onSuccess(res.response, toFetch);
        }, $scope.onError);
    };

    $scope.updateStatus = function (toTrack, response) {
        if(toTrack == "tots")
            $scope.totsStatus = response;
        else if(toTrack == "tows")
            $scope.towsStatus = response;
    };
    $scope.onSuccess = function (trackId, toTrack) {
        debugger;
        $scope.runTracking = function (trackId) {
            var stop = $interval(function () {
                Tracking.get({id: trackId}, function (st) {
                    $scope.updateStatus(toTrack, st.response);
                    if (st.response == "Completed")
                        $interval.cancel(stop);
                }, $scope.onError);
            }, 1000);
        };
        $scope.runTracking(trackId, status);
    };

    $scope.onError = function (err, s) {
        debugger;
        console.log("Error fetch: " + err);
    };
});