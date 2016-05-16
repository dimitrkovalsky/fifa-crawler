package com.liberty.service;

import com.liberty.common.Platform;
import com.liberty.common.RequestHelper;
import com.liberty.model.Player;
import com.liberty.model.Price;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
public class CrawlerService {

  private static final String URL = "http://www.futhead.com/16/players/?bin_platform=pc";

  public void execute() {
    String content = RequestHelper.executeRequestAndGetResult(URL);
    // System.out.println(content);
    List<Player> players = parse(content);
    players.forEach(System.out::println);
  }

  public List<Player> parse(String content) {
    List<Player> players = new ArrayList<>();
    Document document = Jsoup.parse(content);
    Elements playerRows = document.select(".player-row");
    playerRows.forEach(e -> players.add(parseSingle(e)));
//    System.out.println(playerRows);
    return players;
  }

  private Player parseSingle(Element element) {
//    System.out.println(element);
    Player player = new Player();
    player.setId(Long.parseLong(element.attr("data-playerid")));
    String[] splitted = element.select(".name").first().html().split("<br>");
    String name = splitted[0].trim();
    String[] team = removeTags(splitted[1]).split("\\|");
    player.setName(name);
    player.setTeamName(team[0].trim());
    player.setLeagueName(team[1].trim());

    int pace = Integer.parseInt(element.select(".shooting").first().text());
    int shot = Integer.parseInt(element.select(".shooting").last().text());
    player.setPace(pace);
    player.setShooting(shot);
    player.setPassing(Integer.parseInt(element.select(".passing").first().text()));
    player.setDribbling(Integer.parseInt(element.select(".dribbling").first().text()));
    player.setDefending(Integer.parseInt(element.select(".defending").first().text()));
    player.setHeading(Integer.parseInt(element.select(".heading").first().text()));
    player.setTotal(Integer.parseInt(element.select(".sorted").first().text()));
    player.setPosition(element.select(".position").text());

    player.setImage(element.select(".headshot").attr("src"));
    player.setUrl(element.select("a").first().attr("href"));
    player.setPrice(getPrice(element));
    return player;
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
