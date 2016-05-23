package com.liberty.service.impl;

import com.liberty.model.MonitoringResult;
import com.liberty.model.PlayerInfo;
import com.liberty.model.PlayerMonitoring;
import com.liberty.model.PlayerProfile;
import com.liberty.model.Price;
import com.liberty.model.PriceHistory;
import com.liberty.repositories.MonitoringResultRepository;
import com.liberty.repositories.PlayerInfoRepository;
import com.liberty.repositories.PlayerMonitoringRepository;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.service.CrawlerService;
import com.liberty.service.HistoryService;
import com.liberty.service.MonitoringService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import static com.liberty.common.LoggingUtil.info;


@Service
@Slf4j
public class MonitoringServiceImpl implements MonitoringService {

  @Autowired
  private PlayerProfileRepository profileRepository;

  @Autowired
  private PlayerInfoRepository infoRepository;

  @Autowired
  private CrawlerService crawlerService;

  @Autowired
  private HistoryService historyService;

  @Autowired
  private MonitoringResultRepository monitoringResultRepository;

  @Autowired
  private PlayerMonitoringRepository monitoringRepository;

  @Override
  public void monitor(long playerId) {
    try {

      PlayerMonitoring one = monitoringRepository.findOne(playerId);
      if (one != null) {
        log.error("[MONITORING]", "Player with id " + playerId + " under monitoring");
        return;
      }
      PlayerProfile profile = updateInfo(playerId);

      PlayerMonitoring monitoring = new PlayerMonitoring();
      monitoring.setId(playerId);
      PlayerInfo info = profile.getInfo();
      if (info != null)
        monitoring.setSource(info.getSource());
      monitoring.setStartPrice(profile.getPrice());
      monitoringRepository.save(monitoring);
      historyService.recordHistory(profile);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  private PlayerProfile updateInfo(long playerId) {
    return crawlerService.fetchData(playerId, false);
  }

  @Override
  public List<MonitoringResult> getAllResults() {
    return monitoringResultRepository.findAll();
  }

  @Override
  public List<MonitoringResult> getAllMonitored() {
    Set<Long> ids = monitoringRepository.findAll().stream()
        .map(PlayerMonitoring::getId).collect(Collectors.toSet());

    List<MonitoringResult> collect = monitoringResultRepository.findAll().stream().filter(m -> ids.contains(m.getId())).collect
        (Collectors.toList());
    addImages(collect);
    return collect;
  }

  private void addImages(Iterable<MonitoringResult> collect) {
    collect.forEach(mr -> {
      String image = profileRepository.findOne(mr.getId()).getInfo().getImage();
      mr.setImage(image);
    });
  }

  @Override
  public void deleteMonitor(long id) {
    monitoringRepository.delete(id);
  }

  @Override
  public Iterable<MonitoringResult> getAllByIds(List<Long> ids) {
    Iterable<MonitoringResult> all = monitoringResultRepository.findAll(ids);
    addImages(all);
    return all;
  }

  @Override
  public void updatePrices(Consumer<MonitoringResult> onUpdate) {
    monitoringRepository.findAll().parallelStream().forEach(p -> {
      Price currentPrice = getCurrentPrice(p.getId());
      HistoryServiceImpl.RecordResult result = historyService.recordHistory(p.getId(), currentPrice);
      if (result.isPriceChanged())
        onPriceChanged(result.getHistory(), onUpdate);

    });
  }

  private void onPriceChanged(PriceHistory history, Consumer<MonitoringResult> onUpdate) {
    // Toolkit.getDefaultToolkit().beep();
    PlayerProfile one = profileRepository.findOne(history.getId());
    info("PRICE CHANGE", "Price changed for : " + one.getInfo().getName());
    info("PRICE CHANGE", "Start price : " + history.getFirstPrice().getPrice());
    info("PRICE CHANGE", "Current price : " + history.getCurrentPrice().getPrice());
    MonitoringResult monitoringResult = new MonitoringResult(history.getId(), one.getInfo()
        .getName(), history, one.getInfo().getImage());
    monitoringResultRepository.save(monitoringResult);
    onUpdate.accept(monitoringResult);
  }

  private Price getCurrentPrice(Long id) {
    return crawlerService.getCurrentPrice(id);
  }
}
