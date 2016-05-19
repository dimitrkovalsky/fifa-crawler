package com.liberty.processors;

import com.liberty.common.Platform;
import com.liberty.common.RequestHelper;
import com.liberty.common.ValueParser;
import com.liberty.model.PlayerInfo;
import com.liberty.model.Price;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dmytro_Kovalskyi.
 * @since 17.05.2016.
 */
@Slf4j
public class PriceProcessor {

  private static final String PLAYER_URL_PATTERN = "http://www.futhead.com/16/players/%s";

  public Price process(PlayerInfo info) {
    return process(info.getId());
  }

  public Price process(long id) {
    String content = RequestHelper.executeWithJs(String.format(PLAYER_URL_PATTERN, id));
    Document document = Jsoup.parse(content);
    return process(document);
  }

  public Price process(Document document) {
    Price price = new Price();
    fetchPriceForPlatform(document, Platform.PC).ifPresent(price::setPc);
    fetchPriceForPlatform(document, Platform.PS).ifPresent(price::setPs);
    fetchPriceForPlatform(document, Platform.XBOX).ifPresent(price::setXbox);
    return price;
  }

  private Optional<Price.SpecificPrice> fetchPriceForPlatform(Document document, Platform
      platform) {
    try {

      Price.SpecificPrice price = new Price.SpecificPrice();
      price.setPlatform(platform);
      String prefix = "";
      switch (platform) {
        case PC:
          prefix = "pc";
          break;
        case PS:
          prefix = "ps";
          break;
        case XBOX:
          prefix = "xb";
          break;
      }
      Element priceNode = document.select(String.format(".prices.%s-prices.col-md-4.text-center",
          prefix)).first();
      ValueParser.parseInt(priceNode.select(String.format(".font-22.%s-bin-band.margin-top-6", prefix))
          .select(".value").first().text()).ifPresent(price::setPrice);
      price.setLastUpdate(priceNode.select(String.format(".%s-last-update", prefix)).text());
      ValueParser.parseInt(priceNode.select(String.format(".font-14.%s-band-min", prefix))
          .select(".value").first().text()).ifPresent(price::setMinPrice);
      ValueParser.parseInt(priceNode.select(String.format(".font-14.%s-band-max", prefix))
          .select(".value").first().text()).ifPresent(price::setMaxPrice);
      return Optional.of(price);
    } catch (Exception e) {
      log.error(e.getMessage());
      return Optional.empty();
    }
  }
}
