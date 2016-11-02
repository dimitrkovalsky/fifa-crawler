package com.liberty.service.impl;

import com.liberty.model.League;
import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.processors.pojo.Attributes;
import com.liberty.repositories.LeagueRepository;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.repositories.PlayerTradeStatusRepository;
import com.liberty.service.ClassificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dmytro_Kovalskyi.
 * @since 31.10.2016.
 */
@Service
@Slf4j
public class ClassificationServiceImpl implements ClassificationService {

  @Autowired
  private PlayerProfileRepository profileRepository;

  @Autowired
  private LeagueRepository leagueRepository;

  @Autowired
  private PlayerTradeStatusRepository tradeStatusRepository;

  @Override
  public void bestPremierLeague() {
    String league = "ENG 1";
    List<PlayerProfile> profiles = getProfilesByLeague(league);

    log.info("Found " + profiles.size() + " players from " + league + " league");

    List<PlayerProfile> fast = filterByStat("PAC", 90, profiles);
    logFound(fast, true, "PAC > 90");
    addTag(fast, "BPL Fast");

    List<PlayerProfile> shots = filterByStat("SHO", 80, profiles);
    logFound(shots, true, "SHO > 80");
    addTag(shots, "BPL Shot");
  }

  @Override
  public void bestPhysics() {
    String league = "FRA 1";
    List<PlayerProfile> profiles = profileRepository.findAll();

    log.info("Found " + profiles.size() + " players from " + league + " league");

    List<PlayerProfile> fast = filterByStat("PHY", 90, profiles);
    //  List<PlayerProfile> shot = filterByStat("SHO", 80, fast);
    logFound(fast, true, "PHY > 95");

  }

  @Override
  public void mostBalanced() {
    List<PlayerProfile> profiles = profileRepository.findAll();


    int bottomBound = 70;
    List<PlayerProfile> balances = filterByAllStat(bottomBound, profiles);
    //  List<PlayerProfile> shot = filterByStat("SHO", 80, fast);
    logFound(balances, true, "ALL stats > " + bottomBound);

  }

  @Override
  public void bestRBLB() {
    List<PlayerProfile> profiles = profileRepository.findAll();


    profiles = filterByPosition(profiles, "RB", "LB");
    logFound(profiles, "RB and LB");
    List<PlayerProfile> bestBacks = filterByStat("PAC", 90, profiles);
    logFound(profiles, "PAC > 80");
    bestBacks = filterByStat("DEF", 60, bestBacks);
    logFound(profiles, "DEF > 60");
    bestBacks = filterByStat("PHY", 60, bestBacks);
    logFound(profiles, "PHY > 60");

    logFound(bestBacks, true, "BEST BACKS");

  }

  @Override
  public void bestCB() {
    List<PlayerProfile> profiles = profileRepository.findAll();


    profiles = filterByPosition(profiles, "CB");
    logFound(profiles, "RB and LB");
    List<PlayerProfile> bestBacks = filterByStat("PAC", 70, profiles);
    logFound(profiles, "PAC > 70");
    bestBacks = filterByStat("DEF", 80, bestBacks);
    logFound(profiles, "DEF > 80");
    bestBacks = filterByStat("PHY", 80, bestBacks);
    logFound(profiles, "PHY > 80");

    logFound(bestBacks, true, "BEST CB");

  }

  @Override
  public void bestWingers() {

    List<PlayerProfile> profiles = profileRepository.findAll();
    Set<String> position = profiles.stream().map(PlayerProfile::getPosition).collect(Collectors
        .toSet());

    profiles = filterByPosition(profiles, "LW", "RW", "RM", "LM");
    logFound(profiles, "RM and LM");
    List<PlayerProfile> bestBacks = filterByStat("PAC", 85, profiles);
    logFound(profiles, "PAC > 85");
    bestBacks = filterByStat("DRI", 80, bestBacks);
    logFound(profiles, "DRI > 80");
    bestBacks = filterByStat("SHO", 80, bestBacks);
    logFound(profiles, "SHO > 80");

    logFound(bestBacks, true, "best wingers");

    System.out.println(position);

  }

  @Override
  public void bestGermanLeague() {
    String league = "GER 1";
    List<PlayerProfile> profiles = getProfilesByLeague(league);

    log.info("Found " + profiles.size() + " players from " + league + " league");

    List<PlayerProfile> fast = filterByStat("PAC", 90, profiles);
    logFound(fast, true, "PAC > 90");
    addTag(fast, "GER Fast");

    List<PlayerProfile> shots = filterByStat("SHO", 80, profiles);
    logFound(shots, true, "SHO > 80");
    addTag(shots, "GER Shot");
  }

  @Override
  public void bestSpainLeague() {
    String league = "ESP 1";
    List<PlayerProfile> profiles = getProfilesByLeague(league);

    log.info("Found " + profiles.size() + " players from " + league + " league");

    List<PlayerProfile> fast = filterByStat("PAC", 90, profiles);
    logFound(fast, true, "PAC > 90");
    addTag(fast, "ESP Fast");

    List<PlayerProfile> shots = filterByStat("SHO", 80, profiles);
    logFound(shots, true, "SHO > 80");
    addTag(shots, "ESP Shot");
  }

  @Override
  public void bestItalyLeague() {
    String league = "ITA 1";
    List<PlayerProfile> profiles = getProfilesByLeague(league);

    log.info("Found " + profiles.size() + " players from " + league + " league");

    List<PlayerProfile> fast = filterByStat("PAC", 90, profiles);
    logFound(fast, true, "PAC > 90");
    addTag(fast, "ITALY Fast");

    List<PlayerProfile> shots = filterByStat("SHO", 80, profiles);
    logFound(shots, true, "SHO > 80");
    addTag(shots, "ITALY Shot");
  }

  private void addTag(List<PlayerProfile> fast, String tag) {
    fast.forEach(p -> {
      PlayerTradeStatus one = tradeStatusRepository.findOne(p.getId());
      if (one == null) {
        one = createTrade(p);
      }

      one.addTag(tag);
      tradeStatusRepository.save(one);
    });
  }

  private PlayerTradeStatus createTrade(PlayerProfile profile) {
    PlayerTradeStatus tradeStatus = new PlayerTradeStatus();
    tradeStatus.setId(profile.getId());
    tradeStatus.setName(profile.getName());
    tradeStatus.setEnabled(false);
    return tradeStatus;
  }

  private void logFound(List<PlayerProfile> profiles, String... filter) {
    logFound(profiles, false, filter);
  }

  private void logFound(List<PlayerProfile> profiles, boolean print, String... filter) {
    String join = String.join(" AND ", filter);
    log.info("Found " + profiles.size() + " players with " + join);
    profiles.sort(Comparator.comparing(PlayerProfile::getRating).reversed());
    if (print) {
      profiles.forEach(x -> System.out.println(x.lastName + " => " + x.rating));
    }
  }

  private List<PlayerProfile> filterByStat(String statName, Integer bottomBound,
                                           List<PlayerProfile> profiles) {
    return profiles.stream()
        .filter(x -> getAttributeValue(statName, x.getAttributes()) >= bottomBound)
        .collect(Collectors.toList());
  }

  private List<PlayerProfile> filterByPosition(List<PlayerProfile> profiles, String... positions) {
    Set<String> positionSet = Arrays.stream(positions).collect(Collectors.toSet());
    List<PlayerProfile> collect = profiles.stream()
        .filter(x -> positionSet.contains(x.getPosition()))
        .collect(Collectors.toList());
    return collect;
  }

  private List<PlayerProfile> filterByAllStat(Integer bottomBound, List<PlayerProfile> profiles) {
    return profiles.stream()
        .filter(x -> {
          boolean applicable = true;
          for (Attributes a : x.getAttributes()) {
            if (a.value < bottomBound) {
              applicable = false;
            }
          }
          return applicable;
        }).collect(Collectors.toList());
  }

  private int getAttributeValue(String attributeName, List<Attributes> attributes) {
    for (Attributes attr : attributes) {
      if (attr.name.equals("fut.attribute." + attributeName)) {
        return (int) attr.value;
      }
    }
    return 0;
  }


  private List<PlayerProfile> getProfilesByLeague(String leagueName) {
    League league = leagueRepository.findOneByAbbrName(leagueName);
    return profileRepository.findAllByLeagueId(league.getId());
  }

}
