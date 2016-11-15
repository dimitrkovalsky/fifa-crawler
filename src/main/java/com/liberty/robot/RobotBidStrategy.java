package com.liberty.robot;

import com.liberty.model.RobotRequest;
import com.liberty.repositories.RobotRequestRepository;
import com.liberty.rest.request.MarketSearchRequest;

/**
 * @author Dmytro_Kovalskyi.
 * @since 07.11.2016.
 */
public interface RobotBidStrategy {

    MarketSearchRequest buildRequest(int page);

    default void updateRequest() {
    }

    class CheapPlayers implements RobotBidStrategy {

        @Override
        public MarketSearchRequest buildRequest(int page) {
            MarketSearchRequest request = new MarketSearchRequest();
            request.setPage(page);
            request.setQuality("gold");
            request.setMaxPrice(350);
            return request;
        }
    }

    class GoldMediumPlayers implements RobotBidStrategy {

        @Override
        public MarketSearchRequest buildRequest(int page) {
            MarketSearchRequest request = new MarketSearchRequest();
            request.setPage(page);
            request.setQuality("gold");
            request.setMinPrice(1000);
            return request;
        }
    }

    class CustomRequest implements RobotBidStrategy {

        public static final int REQUEST_ID = 1;
        private RobotRequestRepository repository;
        private RobotRequest request;

        public CustomRequest(RobotRequestRepository repository) {
            this.repository = repository;
            updateRequest();
        }

        @Override
        public void updateRequest() {
            request = repository.findOne(REQUEST_ID);
        }

        @Override
        public MarketSearchRequest buildRequest(int page) {
            MarketSearchRequest searchRequest = new MarketSearchRequest();
            searchRequest.setPage(page);
            searchRequest.setMaxPrice(request.getMaxPrice());
            searchRequest.setMinPrice(request.getMinPrice());
            searchRequest.setQuality(request.getQuality());
            searchRequest.setClubId(request.getClubId());
            searchRequest.setLeagueId(request.getLeagueId());
            searchRequest.setNationId(request.getNationId());
            searchRequest.setMaxBuyNowPrice(request.getMaxBuyNowPrice());
            searchRequest.setMinBuyNowPrice(request.getMinBuyNowPrice());
            searchRequest.setPosition(request.getPosition());
            searchRequest.setPlayerId(request.getPlayerId());
            return searchRequest;
        }
    }
}
