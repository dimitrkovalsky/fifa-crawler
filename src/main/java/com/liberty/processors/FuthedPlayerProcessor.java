package com.liberty.processors;

import com.liberty.common.RequestHelper;
import com.liberty.model.PlayerProfile;
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

import static com.liberty.common.LoggingUtil.info;
import static com.liberty.common.ValueParser.parseInt;


/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
public class FuthedPlayerProcessor {

  private static final String URL_PATTERN = "http://www.futhead.com/16/players/%s";

  public PlayerProfile parse(long id) {
    PlayerProfile profile = new PlayerProfile();
    Stats stats = new Stats();
    String url = String.format(URL_PATTERN, id);
    info(this, "Trying to fetch info from : " + url);
    String content = RequestHelper.executeRequestAndGetResult(url);
    Document document = Jsoup.parse(content);

    profile.setOverviewStats(parseOverview(document));
    profile.setStats(parseStats(document));
    System.out.println(profile);
    //  System.out.println(content);
    return profile;
  }

  private Stats parseStats(Document document) {
    Stats stats = new Stats();
    Element statsNode = document.select(".row.player-center-container").first();
    stats.setDefending(parseDefending(statsNode));
    stats.setDribbling(parseDribbling(statsNode));
    stats.setPace(parsePace(statsNode));
    stats.setPassing(parsePassing(statsNode));
    stats.setShooting(parseShooting(statsNode));
    stats.setPhysical(parsePhysical(statsNode));
    return stats;
  }

  private Shooting parseShooting(Element statsNode) {
    Shooting shooting = new Shooting();
    shooting.setFinishing(findByText(statsNode, "Finishing"));
    shooting.setLongShots(findByText(statsNode, "Long Shots"));
    shooting.setPenalties(findByText(statsNode, "Penalties"));
    shooting.setPositioning(findByText(statsNode, "Positioning"));
    shooting.setShooting(findByText(statsNode, "Shooting"));
    shooting.setShotPower(findByText(statsNode, "Shot Power"));
    shooting.setVolleys(findByText(statsNode, "Volleys"));
    return shooting;
  }

  private Passing parsePassing(Element statsNode) {
    Passing passing = new Passing();
    passing.setCrossing(findByText(statsNode, "Crossing"));
    passing.setCurve(findByText(statsNode, "Curve"));
    passing.setFreeKick(findByText(statsNode, "Free Kick"));
    passing.setLongPassing(findByText(statsNode, "Long Passing"));
    passing.setPassing(findByText(statsNode, "Passing"));
    passing.setShortPassing(findByText(statsNode, "Short Passing"));
    passing.setVision(findByText(statsNode, "Vision"));
    return passing;
  }

  private Pace parsePace(Element statsNode) {
    Pace pace = new Pace();
    pace.setAcceleration(findByText(statsNode, "Acceleration"));
    pace.setPace(findByText(statsNode, "Pace"));
    pace.setSprintSpeed(findByText(statsNode, "Sprint Speed"));
    return pace;
  }

  private Dribbling parseDribbling(Element statsNode) {
    Dribbling dribbling = new Dribbling();
    dribbling.setAgility(findByText(statsNode, "Agility"));
    dribbling.setBalance(findByText(statsNode, "Crossing"));
    dribbling.setBallControl(findByText(statsNode, "Ball Control"));
    dribbling.setDribbling(findByText(statsNode, "Dribbling"));
    dribbling.setReactions(findByText(statsNode, "Reactions"));
    return dribbling;
  }

  private Physical parsePhysical(Element statsNode) {
    Physical physical = new Physical();
    physical.setAggression(findByText(statsNode, "Aggression"));
    physical.setJumping(findByText(statsNode, "Jumping"));
    physical.setPhysical(findByText(statsNode, "Physical"));
    physical.setStamina(findByText(statsNode, "Stamina"));
    physical.setStrength(findByText(statsNode, "Strength"));

    return physical;
  }

  private Defending parseDefending(Element statsNode) {
    Defending defending = new Defending();
    defending.setDefending(findByText(statsNode, "Defending"));
    defending.setHeading(findByText(statsNode, "Heading"));
    defending.setInterceptions(findByText(statsNode, "Interceptions"));
    defending.setMarking(findByText(statsNode, "Marking"));
    defending.setSlidingTackle(findByText(statsNode, "Sliding Tackle"));
    defending.setStandingTackle(findByText(statsNode, "Standing Tackle"));
    return defending;
  }

  private OverviewStats parseOverview(Document document) {
    Element overviewNode = document.select(".list-group.list-igs.player-detail-header" +
        ".header-stats").first();
    OverviewStats overviewStats = new OverviewStats();
    overviewStats.setAttackerRating(findByText(overviewNode, "Attacker Rating"));
    overviewStats.setCreatorRating(findByText(overviewNode, "Creator Rating"));
    overviewStats.setDefenderRating(findByText(overviewNode, "Defender Rating"));
    overviewStats.setBeastRating(findByText(overviewNode, "Beast Rating"));
    overviewStats.setHeadingRating(findByText(overviewNode, "Heading Rating"));
    overviewStats.setTotalStats(findByText(overviewNode, "Total Stats"));
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
    return parseInt(select.first().text());
  }

}
