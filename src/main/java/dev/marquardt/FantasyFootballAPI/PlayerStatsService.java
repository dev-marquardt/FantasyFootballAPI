package dev.marquardt.FantasyFootballAPI;

//import databases
import com.google.common.annotations.Beta;
import dev.marquardt.FantasyFootballAPI.database.*;

// imports for logger and  Getter
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//rate limiter import
import com.google.common.util.concurrent.RateLimiter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class PlayerStatsService {
    // set up logging
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PassingStatsRepository passingStatsRepository;

    @Autowired
    private ScrimmageStatsRepository scrimmageStatsRepository;

    @Autowired
    private DefenseStatsRepository defenseStatsRepository;

    @Autowired
    private AdvancedPassingStatsRepository advancedPassingStatsRepository;

    @Autowired
    private AdvancedRushingStatsRepository advancedRushingStatsRepository;

    @Autowired
    private AdvancedReceivingStatsRepository advancedReceivingStatsRepository;

    @Autowired
    private AdvancedDefenseStatsRepository advancedDefenseStatsRepository;

    @Autowired
    private FantasyRankingsRepository fantasyRankingsRepository;

    @Autowired
    private RedZonePassingStatsRepository redZonePassingStatsRepository;

    @Autowired
    private RedZoneRushingStatsRepository redZoneRushingStatsRepository;

    @Autowired
    private RedZoneReceivingStatsRepository redZoneReceivingStatsRepository;

    @Autowired
    private PlayerDatabaseService playerDatabaseService;

    private void updatePlayerStats(){
        logger.info("Updating player stats");

        // set up rate limiter
        RateLimiter rl = RateLimiter.create((20.0/60.0));

        for(String url : Settings.allPFRSeasonStatsURL){
            try {
                rl.acquire();

                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36")
                        .timeout(1000)
                        .get();

                // grab title
                String title = doc.title();

                // load in table
                Element table = doc.selectFirst("table");
                if (table == null) {
                    throw new RuntimeException("Table not found URL: " + url);
                }

                // grab headers
                Elements header = table.select("thead tr:last-child th");
                if (header.isEmpty()) {
                    throw new RuntimeException("Headers not found in table, URL: " + url);
                }

                // populate table headers
                ArrayList<String> tableHeaders = new ArrayList<>();
                for (Element th : header) {
                    tableHeaders.add(th.attr("data-stat").trim());
                }

                // grab table data
                ArrayList<ArrayList<String>> tableData = new ArrayList<>();

                Elements dataRows = table.select("tbody tr:not(.thead)");

                for (Element row : dataRows) {
                    ArrayList<String> rowData = new ArrayList<>();  // Fix: New list per row

                    Elements cells = row.select("th, td");

                    for (Element cell : cells) {
                        String info = cell.text().trim();
                        rowData.add(info.isEmpty() ? "" : info);
                    }

                    if (!rowData.isEmpty()) {  // Skip empty rows
                        tableData.add(rowData);
                    }
                }// end for loop

                Map<Integer, Object> jsonMap = new HashMap<>();

                for (ArrayList<String> row : tableData) {
                    Map<String, Object> dataMap = new HashMap<>();

                    for (int i = 0; i < tableHeaders.size() && i < row.size(); i++) {
                        dataMap.put(tableHeaders.get(i), row.get(i));
                    }

                    jsonMap.put(playerDatabaseService.getPlayerIds(), dataMap);
                }

            } catch (Exception e){

            }
        }// end url for loop
    }// end of method
}
