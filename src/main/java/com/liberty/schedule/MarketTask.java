package com.liberty.schedule;

import com.liberty.service.TradeService;
import com.liberty.websockets.LogController;

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

  @Autowired
  private LogController logController;

  @Scheduled(fixedRate = 200000)
  public void monitor() {
    // fillDatabase();
    logController.info("Trying to check market");
    //tradeService.updatePrices();
    tradeService.checkMarket();
    //tradeService.getUnassigned();
  }

  private void fillDatabase() {
    tradeService.removeAllPlayers();
    simplePlayers();
    richPlayers();
    veryRichPlayers();
  }

  private void veryRichPlayers() {
    add("De Brune", 192985, 12000);
    add("De Gea", 193080, 6000);
    add("Iniesta", 41, 8000);
    add("Rodriges", 198710, 9000);
    add("Boateng", 183907, 10000);
    add("Sergio Ramos", 155862, 18000);
    add("Benzema", 165153, 4500);
    add("Toure", 20289, 6000);
    add("Sanchez", 184941, 12000);
    add("Lewandowsky", 188545, 17000);
    add("Alaba", 197445, 20000);
    add("Modric", 177003, 17000);
    add("Di Maria", 183898, 9000);
    add("Vidal", 181872, 6000);
    add("Tiago Silva", 164240, 8000);
    add("Reus", 188350, 18000);
    add("Ribery", 156616, 13000);

    add("Ibrahimovich", 41236, 33000);
    add("Aguero", 153079, 33000);
    add("Robben", 9014, 45000);
    add("Messi", 158023, 100000);
    add("Neymar", 190871, 100000);
    add("Bale", 173731, 60000);
  }

  private void richPlayers() {
    add("Bellerin", 203747, 1500);
    add("Diego Costa", 179844, 2100);
    add("Carvajal", 204963, 1300);

    add("Isco", 197781, 900);
    add("Dani Alves", 146530, 1500);
    add("Jordi Alba", 189332, 1300);
    add("Dybala", 211110, 1300);
    add("Courtius", 192119, 1600);
    add("Martinez", 196144, 2000);
    add("Griezman", 194765, 2000);
    add("Gotze", 192318, 1300);
    add("Rooney", 54050, 3000);
    add("Fabregas", 162895, 3000);
    add("Silva", 168542, 3300);
    add("Pizscek", 173771, 2200);
    add("Naldo", 171919, 1000);
    add("Aubumeyang", 188567, 3000);
    add("Baines", 163631, 950);
    add("Rodriguez", 193352, 900);
    add("Vardy", 208830L, 950);
    add("Payet", 177388, 1000);
  }

  private void simplePlayers() {
    // England
    add("Mahrez", 204485L, 1400);
    add("Vardy", 208830L, 1500);
    add("Payet", 177388, 1400);
    add("Kompany", 139720, 1300);
    add("Otamendi", 192366, 1200);
    add("Walcot", 164859, 1000);
    add("Baines", 163631, 1000);
    add("Bellerin", 203747, 1800);
    add("Schwenstaiger", 121944, 1000);
    add("Koscelnyi", 165229, 1000);
    add("Coutinho", 189242, 900);
    add("Willian", 180403, 1000);
    add("Sturrige", 171833, 1000);
    add("Sterling", 202652, 1000);
    add("Azpilcueta", 184432, 1000);
    add("Diego Costa", 179844, 2100);
    add("Santi Cazorla", 146562, 900);


    // Spain
    add("Varane", 201535, 900);
    add("Pepe", 120533, 900);
    add("Pique", 152729, 1000);
    add("Turan", 143745, 900);
    add("Busgets", 189511, 1200);
    add("Carvajal", 204963, 1300);
    add("Marcelo", 176676, 1300);
    add("Martinez", 196144, 1000);


    // Germany
    add("Bellarabi", 202857, 850);
    add("Mkhitarian", 192883, 900);
    add("Aubumeyang", 188567, 3000);
    add("Rodriguez", 193352, 1300);

    // France
    add("Kurzava", 201510, 900);
    add("Lacazette", 193301, 1500);
    add("Lavezzi", 159065, 950);
    add("Verrati", 199556, 900);
    add("Pastore", 191180, 950);
    add("Matudi", 170890, 900);
  }

  private void add(String name, long id, int maxPrice) {
    tradeService.addToAutoBuy(name, id, maxPrice);
  }
}