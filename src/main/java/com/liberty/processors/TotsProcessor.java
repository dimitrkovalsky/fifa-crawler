package com.liberty.processors;

import com.liberty.common.RequestHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;

import static com.liberty.common.LoggingUtil.info;

/**
 * User: Dimitr Date: 19.05.2016 Time: 8:46
 */
@Component
@Slf4j
public class TotsProcessor {

  private static final String TOTS_URL = "http://www.futhead.com/tots/?page=%d";

  public void getTotsIds(Consumer<List<Long>> onPageLoaded) {
    int pages = getPages();
    AtomicInteger counter = new AtomicInteger();
    AtomicInteger pageCounter = new AtomicInteger();
    IntStream.range(1, pages + 1).parallel().forEach(i -> {
      try {
        String url = String.format(TOTS_URL, i);
        info(this, "Trying to fetch ids for page #" + i + " from : " + url);
        List<Long> ids = getIds(url);

        info(this, "Fetched ids for : " + ids.size() + " players for : " + url);
        onPageLoaded.accept(ids);
        pageCounter.addAndGet(1);
        counter.addAndGet(ids.size());
        log.info("[TOTS] processed " + pageCounter.get() + " pages from " + pages);
      } catch (Exception e) {
        log.error(e.getMessage());
      }
    });
    log.info("[TOTS] fetched ids for " + counter.get() + " TOTS players");
  }

  private List<Long> getIds(String url) {
    List<Long> ids = new ArrayList<>();
    String content = RequestHelper.executeRequestAndGetResult(url);
    Document document = Jsoup.parse(content);
    document.select(".hvr-grow").forEach(e -> {
      String playerRef = e.select("a").attr("href");
      ids.add(parseId(playerRef));
    });
    return ids;
  }

  private Long parseId(String playerRef) {
    String start = "/16/players/";
    String substring = playerRef.substring(start.length());
    String id = substring.substring(0, substring.indexOf("/"));
    return Long.parseLong(id);
  }

  public int getPages() {
    String content = RequestHelper.executeRequestAndGetResult(String.format(TOTS_URL, 1));
    Document document = Jsoup.parse(content);
    String pages = document.select(".right-nav.pull-right").first().text();
    return Integer.parseInt(pages.substring(pages.indexOf("of") + 3, pages.indexOf(")")).trim());
  }
}
