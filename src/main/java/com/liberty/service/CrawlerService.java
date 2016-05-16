package com.liberty.service;

import com.liberty.common.Platform;
import com.liberty.common.RequestHelper;
import com.liberty.model.PlayerInfo;
import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerStats;
import com.liberty.model.Price;
import com.liberty.processors.FuthedPlayerProcessor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
public class CrawlerService {

  private FuthedPlayerProcessor processor = new FuthedPlayerProcessor();

  private static final String URL = "http://www.futhead.com/16/players/?bin_platform=pc";

  public void execute() {
    String content = RequestHelper.executeRequestAndGetResult(URL);
    // System.out.println(content);
    List<PlayerInfo> playerInfos = parse(content);
//    playerInfos.forEach(System.out::println);
    List<PlayerProfile> list = playerInfos.stream().map(p -> processor.parse(p.getId())).collect(Collectors.toList());
    list.forEach(System.out::println);
  }

  public List<PlayerInfo> parse(String content) {
    List<PlayerInfo> playerInfos = new ArrayList<>();
    Document document = Jsoup.parse(content);
    Elements playerRows = document.select(".player-row");
    playerRows.forEach(e -> playerInfos.add(parseSingle(e)));
//    System.out.println(playerRows);
    return playerInfos;
  }

  private PlayerInfo parseSingle(Element element) {
//    System.out.println(element);
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
    playerInfo.setPrice(getPrice(element));
    return playerInfo;
  }

  private Price getPrice(Element element) {
    Price price = new Price();

    price.setPc(parsePrice(element.select("[data-platform=pc]").text(), Platform.PC));
    price.setPs(parsePrice(element.select("[data-platform=ps]").text(), Platform.PS));
    price.setXbox(parsePrice(element.select("[data-platform=xb]").text(), Platform.XBOX));
    return price;
  }

  private Price.SpecificPrice parsePrice(String text, Platform platform) {
    float price = 0;
    if (text.toLowerCase().contains("m"))
      price = Float.parseFloat(text.toLowerCase().replace("m", "")) * 1000000;
    else if (text.toLowerCase().contains("k"))
      price = Float.parseFloat(text.toLowerCase().replace("k", "")) * 1000;
    else
      price = Float.parseFloat(text);
    Price.SpecificPrice specificPrice = new Price.SpecificPrice();
    specificPrice.setPrice(price);
    specificPrice.setPlatform(platform);
    return specificPrice;
  }


  private String removeTags(String string) {
    String replaced = string.replaceFirst("<span>", "");
    return replaced.replaceFirst("</span>", "");
  }
}
