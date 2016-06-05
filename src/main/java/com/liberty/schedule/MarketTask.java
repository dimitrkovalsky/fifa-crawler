package com.liberty.schedule;

import com.liberty.service.TradeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dmytro_Kovalskyi.
 * @since 19.05.2016.
 */
@Component
@Slf4j
public class MarketTask {

  @Autowired
  private TradeService tradeService;

  @Scheduled(fixedRate = 120000)
  public void monitor() {
  //  addPlayers();
    log.info("Trying to check market");
    tradeService.checkMarket();
  }

  private void addPlayers() {
    tradeService.removeAllPlayers();
    // England
    add("Mahrez", 204485L, 900);
    add("Vardy", 208830L, 1000);
    add("Payet", 177388, 1000);
    add("Kompany", 139720, 1000);
    add("Otamendi", 192366, 1000);
    add("Walcot", 164859, 900);
    add("Baines", 163631, 1000);
    add("Bellerin", 203747, 2000);
    add("Schwenstaiger", 121944, 1100);
    add("Koscelnyi", 165229, 1300);
    add("Coutinho", 189242, 1000);
    add("Willian", 180403, 1000);
    add("Sturrige", 171833, 1000);
    add("Sterling", 202652, 1000);
    add("Azpilcueta", 184432, 1000);
    add("Diego Costa", 179844, 2100);


    // Spain
    add("Varane", 201535, 900);
    add("Pepe", 120533, 900);
    add("Pique", 152729, 1100);
    add("Turan", 143745, 900);
    add("Busgets", 189511, 1200);
    add("Carvajal", 204963, 1300);
    add("Marcelo", 176676, 1300);


    // Germany
    add("Bellarabi", 202857, 900);
    add("Mkhitarian", 192883, 900);
    add("Aubumeyang", 188567, 1500);
    add("Rodriguez", 193352, 900);

    // France
    add("Kurzava", 201510, 900);
    add("Lacazette", 193301, 1500);
    add("Lavezzi", 159065, 950);
    add("Verrati", 199556, 900);
    add("Verrati", 199556, 900);
    add("Pastore", 191180, 950);
    add("Matudi", 170890, 1000);
  }

  private void add(String name, long id, int maxPrice) {
    tradeService.addToAutoBuy(name, id, maxPrice);
  }

}
