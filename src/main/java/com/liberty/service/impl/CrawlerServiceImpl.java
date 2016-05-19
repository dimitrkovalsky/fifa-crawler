package com.liberty.service.impl;

import com.liberty.common.RequestHelper;
import com.liberty.model.PlayerInfo;
import com.liberty.model.PlayerProfile;
import com.liberty.model.Price;
import com.liberty.processors.FutheadPlayerProcessor;
import com.liberty.processors.FutheadTableDataProcessor;
import com.liberty.processors.PriceProcessor;
import com.liberty.processors.TotsProcessor;
import com.liberty.repositories.PlayerInfoRepository;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.service.CrawlerService;
import com.liberty.service.MonitoringService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
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

  public static final int DEFAULT_PAGE_SIZE = 20;
  @Autowired
  private PlayerInfoRepository infoRepository;

  @Autowired
  private PlayerProfileRepository profileRepository;

  @Autowired
  private TotsProcessor totsProcessor;

  @Autowired
  private MonitoringService monitoringService;

  private PriceProcessor priceProcessor = new PriceProcessor();

  private FutheadPlayerProcessor processor = new FutheadPlayerProcessor();

  private static final String PLAYERS_URL = "http://www.futhead.com/16/players/?page=%d&bin_platform=pc";

  @Override
  public void execute() {
    //fetchBaseData();
    fetchFullInfo();
  }

  @Override
  public void monitorTots() {
    totsProcessor.getTotsIds(ids -> ids.parallelStream().forEach(monitoringService::monitor));
  }

  @Override
  public PlayerProfile fetchData(long playerId) {
    PlayerProfile profile = processor.fetchInfo(playerId);
    return profileRepository.save(profile);
  }

  @Override
  public Price getCurrentPrice(Long id) {
    return priceProcessor.process(id);
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
