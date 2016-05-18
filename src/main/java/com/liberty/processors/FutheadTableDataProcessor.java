package com.liberty.processors;

import com.liberty.common.Platform;
import com.liberty.common.RequestHelper;
import com.liberty.model.PlayerInfo;
import com.liberty.model.PlayerStats;
import com.liberty.model.Price;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FutheadTableDataProcessor {

  private List<PlayerInfo> parse(String content) {
    try {
      List<PlayerInfo> playerInfos = new ArrayList<>();
      Document document = Jsoup.parse(content);
      Elements playerRows = document.select(".player-row");
      playerRows.forEach(e -> parseSingle(e).ifPresent(playerInfos::add));
      return playerInfos;
    } catch (Exception e) {
      log.error(e.getMessage());
      return Collections.emptyList();
    }
  }

  private Optional<PlayerInfo> parseSingle(Element element) {
    try {
      PlayerInfo playerInfo = new PlayerInfo();
      playerInfo.setId(Long.parseLong(element.attr("data-playerid")));
      String[] splitted = element.select(".name").first().html().split("<br>");
      String name = splitted[0].trim();
      String[] team = removeTags(splitted[1]).split("\\|");
      playerInfo.setName(name);
      playerInfo.setTeamName(team[0].trim());
      playerInfo.setLeagueName(team[1].trim());

      int pace = Integer.parseInt(element.select(".shooting").first().text());
      int shot = Integer.parseInt(element.select(".shooting").last().text());
      PlayerStats stats = new PlayerStats();
      stats.setPace(pace);
      stats.setShooting(shot);
      stats.setPassing(Integer.parseInt(element.select(".passing").first().text()));
      stats.setDribbling(Integer.parseInt(element.select(".dribbling").first().text()));
      stats.setDefending(Integer.parseInt(element.select(".defending").first().text()));
      stats.setHeading(Integer.parseInt(element.select(".heading").first().text()));
      stats.setTotal(Integer.parseInt(element.select(".sorted").first().text()));
      playerInfo.setStats(stats);

      playerInfo.setPosition(element.select(".position").text());
      playerInfo.setImage(element.select(".headshot").attr("src"));
      playerInfo.setUrl(element.select("a").first().attr("href"));
      getPrice(element).ifPresent(playerInfo::setPrice);
      return Optional.of(playerInfo);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private Optional<Price> getPrice(Element element) {
    try {

      Price price = new Price();
      price.setPc(parsePrice(element.select("[data-platform=pc]").text(), Platform.PC));
      price.setPs(parsePrice(element.select("[data-platform=ps]").text(), Platform.PS));
      price.setXbox(parsePrice(element.select("[data-platform=xb]").text(), Platform.XBOX));
      return Optional.of(price);
    } catch (Exception e) {
      log.error(e.getMessage());
      return Optional.empty();
    }
  }

  private Price.SpecificPrice parsePrice(String text, Platform platform) {
    Price.SpecificPrice specificPrice = new Price.SpecificPrice();
    try {
      float price = 0;
      if (text.equals("???"))
        return specificPrice;
      if (text.toLowerCase().contains("m")) {
        price = Float.parseFloat(text.toLowerCase().replace("m", "")) * 1000000;
      } else if (text.toLowerCase().contains("k")) {
        price = Float.parseFloat(text.toLowerCase().replace("k", "")) * 1000;
      } else
        price = Float.parseFloat(text);

      if (price != 0) {
        specificPrice.setPrice((int) price);
      }
      specificPrice.setPlatform(platform);
      return specificPrice;
    } catch (Exception e) {
      log.error(e.getMessage());
      return specificPrice;
    }
  }

  private String removeTags(String string) {
    String replaced = string.replaceFirst("<span>", "");
    return replaced.replaceFirst("</span>", "");
  }

  public List<PlayerInfo> process(String url) {
    String content = RequestHelper.executeRequestAndGetResult(url);
    return parse(content);
  }
}
