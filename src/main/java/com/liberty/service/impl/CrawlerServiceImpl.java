package com.liberty.service.impl;

import com.liberty.common.RequestHelper;
import com.liberty.model.PlayerInfo;
import com.liberty.model.PlayerProfile;
import com.liberty.model.Price;
import com.liberty.model.Source;
import com.liberty.processors.FutheadPlayerProcessor;
import com.liberty.processors.FutheadTableDataProcessor;
import com.liberty.processors.InformProcessor;
import com.liberty.processors.PriceProcessor;
import com.liberty.repositories.PlayerInfoRepository;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.repositories.SourceRepository;
import com.liberty.service.CrawlerService;
import com.liberty.service.MonitoringService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;

import static com.liberty.common.LoggingUtil.info;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
@Component
@Slf4j
public class CrawlerServiceImpl implements CrawlerService {

  private Map<String, String> statuses = new HashMap<>();

  public static final int DEFAULT_PAGE_SIZE = 20;
  @Autowired
  private PlayerInfoRepository infoRepository;

  @Autowired
  private PlayerProfileRepository profileRepository;

  @Autowired
  private InformProcessor informProcessor;

  @Autowired
  private SourceRepository sourceRepository;

  @Autowired
  private MonitoringService monitoringService;

  private PriceProcessor priceProcessor = new PriceProcessor();

  private FutheadPlayerProcessor processor = new FutheadPlayerProcessor();

  private static final String PLAYERS_URL = "http://www.futhead.com/16/players/?page=%d&bin_platform=pc";

  @Override
  public void execute() {
    //fetchBaseData();
    //fetchFullInfo();
  }

  @Override
  public String fetchSources() {
    Set<String> sources = profileRepository.findAll().stream().map(p -> {
      PlayerInfo info = p.getInfo();
      if (info == null || info.getSource() == null)
        return Optional.<String>empty();
      return Optional.of(info.getSource());
    }).filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toSet());
    Set<Source> toSave = sources.stream().map(Source::new).collect(Collectors.toSet());
    sourceRepository.deleteAll();
    sourceRepository.save(toSave);
    return "";
  }

  @Override
  public String getStatus(String id) {
    return statuses.get(id);
  }

  @Override
  public PlayerProfile fetchData(long playerId, boolean force) {
    PlayerProfile profile;
    if (!force) {
      profile = profileRepository.findOne(playerId);
      if (profile != null)
        return profile;
    }
    profile = processor.fetchInfo(playerId);
    return profileRepository.save(profile);
  }

  private PlayerProfile fetchData(long playerId) {
    return fetchData(playerId, false);
  }

  @Override
  public Price getCurrentPrice(Long id) {
    return priceProcessor.process(id);
  }

  @Override
  public String fetchTots() {
    String trackId = UUID.randomUUID().toString();
    new Thread(() ->
        informProcessor.getTotsIds((ids, status) -> processPage(ids, status, trackId))
    ).start();
    return trackId;
  }

  private Void processPage(List<Long> ids, String status, String trackId) {
    statuses.put(trackId, status);
    AtomicInteger counter = new AtomicInteger();
    ids.parallelStream().forEach(id -> {
      fetchData(id);
      int current = counter.getAndIncrement();
      String currentStatus = status + " processed " + current + " / " + ids.size() + " players";
      statuses.put(trackId, currentStatus);
    });
    return null;
  }

  @Override
  public String fetchTows() {
    String trackId = UUID.randomUUID().toString();
    new Thread(() ->
        informProcessor.getTotwIds((ids, status) -> processPage(ids, status, trackId))
    ).start();
    return trackId;
  }

  private void fetchBaseData() {
    int pages = getPages();
    FutheadTableDataProcessor tableDataProcessor = new FutheadTableDataProcessor();
    AtomicInteger counter = new AtomicInteger();
    AtomicInteger pageCounter = new AtomicInteger();
    IntStream.range(1, pages + 1).parallel().forEach(i -> {
      try {
        String url = String.format(PLAYERS_URL, i);
        info(this, "Trying to fetch info for page #" + i + " from : " + url);
        List<PlayerInfo> playerInfos = tableDataProcessor.process(url);

        info(this, "Fetched info for : " + playerInfos.size() + " players for : " + url);
        infoRepository.save(playerInfos);
        info(this, "Stored info for : " + playerInfos.size() + " players for : " + url);
        pageCounter.addAndGet(1);
        counter.addAndGet(playerInfos.size());
        log.info("[CRAWLER] processed " + pageCounter.get() + " pages from " + pages);
      } catch (Exception e) {
        log.error(e.getMessage());
      }
    });
    log.info("[CRAWLER] fetched base info for " + counter.get() + " players");
  }

  private void fetchFullInfo() {
    int pages = (int) infoRepository.count() / DEFAULT_PAGE_SIZE;
    AtomicInteger counter = new AtomicInteger();
    AtomicInteger pageCounter = new AtomicInteger();
    IntStream.range(0, pages + 1).parallel().forEach(i -> {
      try {
        List<PlayerInfo> infos = infoRepository.findAll(new PageRequest(i,
            DEFAULT_PAGE_SIZE)).getContent();
        String url = String.format(PLAYERS_URL, i);
        log.info("[DEEP CRAWLER] Trying to fetch full info for page #" + i + " from : " + url);
        List<PlayerProfile> profiles = infos.parallelStream().map(p -> processor.parse(p)).collect
            (Collectors.toList());
        profileRepository.save(profiles);
        pageCounter.addAndGet(1);
        counter.addAndGet(profiles.size());
        log.info("[DEEP CRAWLER] processed " + pageCounter.get() + " pages from " + pages);
      } catch (Exception e) {
        log.error(e.getMessage());
      }
    });
    log.info("[DEEP CRAWLER] fetched base info for " + counter.get() + " players");
  }


  private int getPages() {
    String content = RequestHelper.executeRequestAndGetResult(String.format(PLAYERS_URL, 1));
    Document document = Jsoup.parse(content);
    String pages = document.select(".right-nav.pull-right").first().text();
    return Integer.parseInt(pages.substring(pages.indexOf("of") + 3, pages.indexOf(")")).trim());
  }
}
