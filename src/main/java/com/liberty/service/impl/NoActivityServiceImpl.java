package com.liberty.service.impl;

import com.liberty.common.DelayHelper;
import com.liberty.model.PlayerInfo;
import com.liberty.service.NoActivityService;
import com.liberty.service.PriceService;
import com.liberty.service.RequestService;
import com.liberty.service.TagService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dmytro_Kovalskyi.
 * @since 25.10.2016.
 */
@Service
@Slf4j
public class NoActivityServiceImpl implements NoActivityService {

  public static final String TAG_TO_UPDATE = "cheap";
  public static final int TO_UPDATE_PLAYERS_AMOUNT = 5;
  public static final int REQUEST_PER_MINUTE = 30;
  private int currentSkip = 110;
  private boolean completed = false;

  @Autowired
  private RequestService requestService;

  @Autowired
  private TagService tagService;

  @Autowired
  private PriceService priceService;

  @Override
  public void updatePlayerPrices() {
    if (completed) {
      log.info("Completed price update for tag : " + TAG_TO_UPDATE);
      return;
    }
    List<PlayerInfo> players = tagService.getByTag(TAG_TO_UPDATE);
    if (currentSkip >= players.size()) {
      completed = true;
      return;
    }

    List<PlayerInfo> toUpdate = players.stream().skip(currentSkip).limit(TO_UPDATE_PLAYERS_AMOUNT)
        .collect(Collectors.toList());
    for (PlayerInfo info : toUpdate) {
      priceService.findMinPrice(info.getProfile().getId());
      currentSkip++;
      if (requestService.getRequestRate() >= REQUEST_PER_MINUTE) {
        break;
      }
      DelayHelper.wait(2000, 100);
    }
    log.info("[NoActivityServiceImpl] Successfully updated players from skip : " + currentSkip);

  }

}
