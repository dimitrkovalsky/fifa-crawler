package com.liberty.processors;

import com.liberty.common.RequestHelper;
import com.liberty.model.Squad;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.liberty.common.LoggingUtil.info;


/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
@Slf4j
public class FutheadPlayerProcessor {

    private static final String URL_PATTERN = "http://www.futhead.com/17/players/%s";
    public static final String SQUAD_URL = "http://www.futhead.com/squad-building-challenges/squads/";


    public Optional<Long> fetchFifaId(long id) {
        try {

            String url = String.format(URL_PATTERN, id);
            info(this, "Trying to fetch info from : " + url);
            String content = RequestHelper.executeWithJs(url);
            Document document = Jsoup.parse(content);

            Elements select = document.select("[data-eaid]");
            System.out.println(select);
        } catch (Exception e) {

        }
        return Optional.empty();
    }


    public Squad fetchBaseSquadInfo(Long squadId) {
        String url = SQUAD_URL + squadId;
        String content = RequestHelper.executeRequestAndGetResult(url);
        Document document = Jsoup.parse(content);
        String toFetch = "";
        String squadName = "";
        String groupName = "";
        int nameEnd;
        try {

            Elements nodes = document.select(".futhead").select(".small-head");
            if (nodes == null || nodes.isEmpty()) {
                nodes = document.select(".futhead").select(".has-small");
                toFetch = nodes.get(0).toString();
                int nameStart = toFetch.indexOf("has-small");
                nameEnd = toFetch.indexOf("<small>");
                squadName = toFetch.substring(nameStart + 12, nameEnd).trim();
            } else {
                toFetch = nodes.get(0).toString();
                int nameStart = toFetch.indexOf("small-head");
                nameEnd = toFetch.indexOf("<small>");
                squadName = toFetch.substring(nameStart + 13, nameEnd).trim();

            }
            if (!squadName.isEmpty() && squadName.contains(">")) {
                squadName = squadName.substring(squadName.indexOf(">" + 1));
            }
            String groupString = toFetch.substring(nameEnd + 8, toFetch.indexOf("</small>"));
            groupName = groupString.substring(groupString.indexOf(">") + 1).trim();
        } catch (Exception e) {
            log.error("Can not parse futhead : " + e.getMessage());
        }
        Squad squad = new Squad();
        squad.setSquadGroup(groupName);
        squad.setSquadName(squadName);
        return squad;
    }


    public List<Long> getPlayerIds(Long squadId) {
        String url = SQUAD_URL + squadId;
        List<Long> fifaIds = new ArrayList<>();
        try {

            String content = RequestHelper.executeRequestAndGetResult(url);
            int start = content.indexOf("$.parseJSON");
            int end = content.indexOf("$('#edit-squad'");
            String toSearch = content.substring(start, end);
            int startIndex = toSearch.indexOf("\"player_id\":");
            while (startIndex > 0) {
                String substring = toSearch.substring(startIndex, startIndex + 25);
                int idIndex = substring.indexOf(":");
                int idIndex2 = substring.indexOf(",");
                String id = substring.substring(idIndex + 1, idIndex2).trim();
                fifaIds.add(Long.parseLong(id));
                toSearch = toSearch.substring(startIndex + substring.length());
                startIndex = toSearch.indexOf("\"player_id\":");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return fifaIds;
    }

}
