fifaApp.controller('MarketSearchController', function ($rootScope, $scope, MarketSearch) {
    $scope.search = {};
    $scope.search.quality = "gold";
    $scope.search.page = 0;
    $scope.resultPresent = false;

    $scope.onSearchResult = function(result) {
       $scope.resultPresent = true;
       $scope.trades = result.items;
    };

    $scope.performSearch = function () {
        MarketSearch.save($scope.search, $scope.onSearchResult, $rootScope.onError);
    };

    $scope.shouldBid = function(trade) {
        if(trade.auctionInfo.currentBid < trade.tradeStatus.maxPrice &&
            trade.auctionInfo.startingBid < trade.tradeStatus.maxPrice ){
            return true;
        }
        return false;
    };

     $scope.shouldBuy = function(trade) {
         if(trade.auctionInfo.buyNowPrice < trade.tradeStatus.maxPrice){
             return true;
         }
         return false;
     };

     $scope.hasMoreElements = function() {
        return true;
     };

    $scope.isFirst = function() {
       return $scope.search.page == 0;
    };

    $scope.resetPage = function() {
       $scope.search.page = 0;
    };

    $scope.nextPage = function() {
       $scope.search.page = $scope.search.page + 1;
       $scope.performSearch();
    };

    $scope.previousPage = function() {
       if($scope.search.page <= 0){
         console.log("Can not use page less than 0");
         return;
       }
       $scope.search.page = $scope.search.page - 1;
       $scope.performSearch();
    };

});