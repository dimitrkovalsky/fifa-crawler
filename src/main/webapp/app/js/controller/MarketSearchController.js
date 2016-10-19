fifaApp.controller('MarketSearchController', function ($rootScope, $scope, MarketSearch,
Suggestions, Bid) {
    $scope.search = {};
    $scope.search.quality = "gold";
    $scope.search.page = 0;
    $scope.resultPresent = false;

    $scope.suggestions = [];

    $rootScope.readSuggestions = function () {
        Suggestions.getData(function (data) {
            $scope.suggestions = [];
            angular.forEach(data.Players, function(value, key) {
                var pl = {
                    id: value.id,
                    first: value.f,
                    last: value.l,
                    title: value.c || value.l,
                    rating: value.r,
                    nation: value.n,
                };
                if(pl.title != undefined){
                    $scope.suggestions.push(pl);
                }
            });

            $(function () {
                var ms = $('#magicsuggest').magicSuggest({
                    data: $scope.suggestions,
                    maxSuggestions: 5,
                    maxSelection: 1,
                    maxDropHeight: 500,
                    style: 'width: 500px !important',
                    valueField: 'id',
                    sortDir: 'desc',
                    sortOrder: 'rating',
                    displayField: 'title',
                    renderer: function (data) {
                        return '<div class="player-suggest" style="display: flex;">' +
                            '<div style="width: 90%;"><div class="name">' + data.title +'(' +
                            data.last + ')' + '</div> '  +
                           '<div class="nation-suggest" >'+ '<img src="/api/images/nation/' +  data.nation + '">' +
                            '<div class="rating" style="display: inline-block; padding:10px">' +
                            data.rating + '</div></div> </div>' +
                            '<div class="picture-suggest"> <img class="image-suggest"  src="/api/images/' + data.id +'">' +
                            '</div>'+
                            '</div>';
                    }
                });

                $(ms).on('selectionchange', function(){
                    var selected = this.getSelection();
                    if(selected.length <= 0){
                        $scope.search.playerId = null;
                    } else {
                       $scope.search.playerId = selected[0].id;
                    }
                });
            });
        });
    };


    $scope.onSearchResult = function (result) {
        $scope.resultPresent = true;
        $scope.trades = result.items;
    };

    $scope.performSearch = function () {
        MarketSearch.save($scope.search, $scope.onSearchResult, $rootScope.onError);
    };

    $scope.shouldBid = function (trade) {
        if (trade.tradeStatus && trade.auctionInfo.currentBid < trade.tradeStatus.maxPrice &&
            trade.auctionInfo.startingBid < trade.tradeStatus.maxPrice) {
            return true;
        }
        return false;
    };

    $scope.shouldBuy = function (trade) {
        if (trade.tradeStatus && trade.auctionInfo.buyNowPrice < trade.tradeStatus.maxPrice) {
            return true;
        }
        return false;
    };

    $scope.hasMoreElements = function () {
        return true;
    };

    $scope.isFirst = function () {
        return $scope.search.page == 0;
    };

    $scope.isMonitored = function(trade) {
      return trade.tradeStatus != null;
    };

    $scope.resetPage = function () {
        $scope.search.page = 0;
    };

    $scope.nextPage = function () {
        $scope.search.page = $scope.search.page + 1;
        $scope.performSearch();
    };

    $scope.makeBid = function(trade) {
       $scope.performBid(trade.auctionInfo.tradeId, trade.auctionInfo.buyNowPrice);
    };

    $scope.buyNow = function(trade) {
        $scope.performBid(trade.auctionInfo.tradeId, $scope.defineTradeBid(trade));
    };

    $scope.performBid = function(tradeId, bid) {
      Bid.save({
          tradeId: tradeId,
          bid: bid
      }, function(result) {
          console.log(result);
      }, $rootScope.onError);
    };

     $scope.defineTradeBid = function(trade) {
          if(trade.auctionInfo.currentBid == 0) {
            return trade.auctionInfo.startingBid;
          }
          else {
             return  $scope.defineBid(trade.auctionInfo.currentBid);
          }
     };

    $scope.defineBid = function(currentBid) {
        if (currentBid < 1000) {
          return currentBid + 50;
        } else if (currentBid <= 3000) {
          return currentBid + 100;
        } else if (currentBid <= 5000) {
          return currentBid + 300;
        } else if (currentBid < 10000) {
          return currentBid + 500;
        } else if (currentBid < 50000) {
          return currentBid + 1500;
        } else if (currentBid < 100000) {
          return currentBid + 2500;
        } else {
          return currentBid + 5000;
        }
    };

    $scope.previousPage = function () {
        if ($scope.search.page <= 0) {
            console.log("Can not use page less than 0");
            return;
        }
        $scope.search.page = $scope.search.page - 1;
        $scope.performSearch();
    };
    $scope.readSuggestions();
});