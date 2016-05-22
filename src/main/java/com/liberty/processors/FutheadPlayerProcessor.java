package com.liberty.processors;

import com.liberty.common.RequestHelper;
import com.liberty.model.PlayerInfo;
import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerStats;
import com.liberty.model.Price;
import com.liberty.model.stats.Defending;
import com.liberty.model.stats.Dribbling;
import com.liberty.model.stats.OverviewStats;
import com.liberty.model.stats.Pace;
import com.liberty.model.stats.Passing;
import com.liberty.model.stats.Physical;
import com.liberty.model.stats.Shooting;
import com.liberty.model.stats.Stats;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lombok.extern.slf4j.Slf4j;

import static com.liberty.common.LoggingUtil.info;
import static com.liberty.common.ValueParser.parseInt;


/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
@Slf4j
public class FutheadPlayerProcessor {

  private static final String URL_PATTERN = "http://www.futhead.com/16/players/%s";
  private PriceProcessor priceProcessor = new PriceProcessor();

  public PlayerProfile parse(PlayerInfo player) {
    PlayerProfile profile = new PlayerProfile();
    Stats stats = new Stats();
    String url = String.format(URL_PATTERN, player.getId());
    info(this, "Trying to fetch info from : " + url);
    String content = RequestHelper.executeWithJs(url);
    Document document = Jsoup.parse(content);

    profile.setOverviewStats(parseOverview(document));
    profile.setStats(parseStats(document));
    profile.setInfo(parseInfo(document, profile.getStats(), player.getId()));
    profile.setId(player.getId());
    profile.setPrice(priceProcessor.process(document));
    info(this, "Parsed full info for : " + player.getName());
    return profile;
  }

  public PlayerProfile fetchInfo(long id) {
    PlayerProfile profile = new PlayerProfile();
    try {

      String url = String.format(URL_PATTERN, id);
      info(this, "Trying to fetch info from : " + url);
      String content = RequestHelper.executeWithJs(url);
      Document document = Jsoup.parse(content);

      profile.setOverviewStats(parseOverview(document));
      profile.setStats(parseStats(document));
      profile.setInfo(parseInfo(document, profile.getStats(), id));
      profile.setId(id);
      profile.setPrice(priceProcessor.process(document));
      info(this, "Parsed full info for : " + profile.getInfo().getName());
      return profile;
    } catch (Exception e) {
      log.error(e.getMessage());
      return profile;
    }
  }

  private PlayerInfo parseInfo(Document document, Stats fullStats, long id) {
    PlayerInfo info = new PlayerInfo();
    Element infoNode = document.select(".player-stats-container").first();
    info.setStats(parsePlayerStats(infoNode, fullStats, document));
    info.setPosition(document.select(".playercard-position").first().text());
    info.setImage(document.select(".playercard-picture img").first().attr("src"));
    Elements table = document.select(".content-box .table.table-striped.table-condensed.table-borderless").first().child(0).children();

    info.setId(id);
    info.setName(table.get(0).child(0).text());
    info.setTeamName(table.get(1).child(1).text());
    info.setLeagueName(table.get(2).child(1).text());
    info.setNation(table.get(3).child(1).text());
    info.setSource(table.get(4).child(1).text());
    info.setPlayCardPicture(document.select(".playercard-picture").first().select("img").attr("src"));

    info.setPrice(parsePrice(document));
    return info;
  }

  private Price parsePrice(Document document) {
    return priceProcessor.process(document);
  }

  private PlayerStats parsePlayerStats(Element node, Stats fullStats, Document document) {
    PlayerStats stats = new PlayerStats();

    int skillMoves = parseInt(node.getElementsContainingOwnText("Skill Moves").first().text()
        .split(":")[1].trim()).orElse(0);
    int weakFoot = parseInt(node.getElementsContainingOwnText("Weak Foot").first().text().split(":")[1].trim()).orElse(0);
    String strongFoot = node.getElementsContainingOwnText("Strong Foot").first().text().split(":")[1].trim();
    stats.setSkillMoves(skillMoves);
    stats.setWeakFoot(weakFoot);
    stats.setStrongFoot(strongFoot);
    stats.setDefending(fullStats.getDefending().getDefending());
    stats.setDribbling(fullStats.getDribbling().getDribbling());
    stats.setPace(fullStats.getPace().getPace());
    stats.setPassing(fullStats.getPassing().getPassing());
    stats.setShooting(fullStats.getShooting().getShooting());
    stats.setHeading(fullStats.getPhysical().getPhysical());
    stats.setTotal(Integer.parseInt(document.select(".player-detail-card-large .playercard-rating")
        .first().text()));
    return stats;
  }

  private Stats parseStats(Document document) {
    Stats stats = new Stats();
    try {
      Element statsNode = document.select(".row.player-center-container").first();
      stats.setDefending(parseDefending(statsNode));
      stats.setDribbling(parseDribbling(statsNode));
      stats.setPace(parsePace(statsNode));
      stats.setPassing(parsePassing(statsNode));
      stats.setShooting(parseShooting(statsNode));
      stats.setPhysical(parsePhysical(statsNode));
    } catch (Exception e) {
      log.error("Can not parse Stats");
    }
    return stats;
  }

  private Shooting parseShooting(Element statsNode) {
    Shooting shooting = new Shooting();
    try {

      shooting.setFinishing(findByText(statsNode, "Finishing"));
      shooting.setLongShots(findByText(statsNode, "Long Shots"));
      shooting.setPenalties(findByText(statsNode, "Penalties"));
      shooting.setPositioning(findByText(statsNode, "Positioning"));
      shooting.setShooting(findByText(statsNode, "Shooting"));
      shooting.setShotPower(findByText(statsNode, "Shot Power"));
      shooting.setVolleys(findByText(statsNode, "Volleys"));
    } catch (Exception e) {
      log.error("Can not parse Shooting");
    }
    return shooting;
  }

  private Passing parsePassing(Element statsNode) {
    Passing passing = new Passing();
    try {

      passing.setCrossing(findByText(statsNode, "Crossing"));
      passing.setCurve(findByText(statsNode, "Curve"));
      passing.setFreeKick(findByText(statsNode, "Free Kick"));
      passing.setLongPassing(findByText(statsNode, "Long Passing"));
      passing.setPassing(findByText(statsNode, "Passing"));
      passing.setShortPassing(findByText(statsNode, "Short Passing"));
      passing.setVision(findByText(statsNode, "Vision"));
    } catch (Exception e) {
      log.error("Can not parse Passing");
    }
    return passing;
  }

  private Pace parsePace(Element statsNode) {
    Pace pace = new Pace();
    try {

      pace.setAcceleration(findByText(statsNode, "Acceleration"));
      pace.setPace(findByText(statsNode, "Pace"));
      pace.setSprintSpeed(findByText(statsNode, "Sprint Speed"));
    } catch (Exception e) {
      log.error("Can not parse Pace");
    }
    return pace;
  }

  private Dribbling parseDribbling(Element statsNode) {
    Dribbling dribbling = new Dribbling();
    try {
      dribbling.setAgility(findByText(statsNode, "Agility"));
      dribbling.setBalance(findByText(statsNode, "Crossing"));
      dribbling.setBallControl(findByText(statsNode, "Ball Control"));
      dribbling.setDribbling(findByText(statsNode, "Dribbling"));
      dribbling.setReactions(findByText(statsNode, "Reactions"));
    } catch (Exception e) {
    }
    return dribbling;
  }

  private Physical parsePhysical(Element statsNode) {
    Physical physical = new Physical();
    try {
      physical.setAggression(findByText(statsNode, "Aggression"));
      physical.setJumping(findByText(statsNode, "Jumping"));
      physical.setPhysical(findByText(statsNode, "Physical"));
      physical.setStamina(findByText(statsNode, "Stamina"));
      physical.setStrength(findByText(statsNode, "Strength"));
    } catch (Exception e) {
      log.error("Can not parse Physical");
    }
    return physical;
  }

  private Defending parseDefending(Element statsNode) {
    Defending defending = new Defending();
    try {

      defending.setDefending(findByText(statsNode, "Defending"));
      defending.setHeading(findByText(statsNode, "Heading"));
      defending.setInterceptions(findByText(statsNode, "Interceptions"));
      defending.setMarking(findByText(statsNode, "Marking"));
      defending.setSlidingTackle(findByText(statsNode, "Sliding Tackle"));
      defending.setStandingTackle(findByText(statsNode, "Standing Tackle"));
    } catch (Exception e) {
      log.error("Can not parse Defending");
    }
    return defending;
  }

  private OverviewStats parseOverview(Document document) {
    OverviewStats overviewStats = new OverviewStats();
    try {
      Element overviewNode = document.select(".list-group.list-igs.player-detail-header" +
          ".header-stats").first();
      overviewStats.setAttackerRating(findByText(overviewNode, "Attacker Rating"));
      overviewStats.setCreatorRating(findByText(overviewNode, "Creator Rating"));
      overviewStats.setDefenderRating(findByText(overviewNode, "Defender Rating"));
      overviewStats.setBeastRating(findByText(overviewNode, "Beast Rating"));
      overviewStats.setHeadingRating(findByText(overviewNode, "Heading Rating"));
      overviewStats.setTotalStats(findByText(overviewNode, "Total Stats"));
    } catch (Exception e) {
      log.error("Can not parse OverviewStats");
    }
    return overviewStats;
  }

  private int findByText(Element node, String toSearch) {
    Elements select = node.getElementsContainingOwnText(toSearch)
        .select(".pull-right");
//    if(select.size() != 1 ) {
//      System.out.println("TO search : " + toSearch);
//      System.out.println(select);
//
//    }
    return parseInt(select).orElse(0);
  }

}
