package dev.marquardt.FantasyFootballAPI;

//import databases
import dev.marquardt.FantasyFootballAPI.database.*;

// imports for logger and  Getter
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
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

import java.util.*;

@Service
public class PlayerStatsService {
    // set up logging
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // map to hold name display stuff
    private final Map<String, String> nameKey = new HashMap<String, String>();

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

    @PostConstruct
    public void initNameKeyMappings() {
        nameKey.clear();

        nameKey.put("2025_NFL_Passing", "name_display");
        nameKey.put("2025_NFL_Scrimmage_Stats","name_display");
        nameKey.put("2025_NFL_Defense","name_display");
        nameKey.put("2025_NFL_Advanced_Passing","name_display");
        nameKey.put("2025_NFL_Advanced_Rushing","name_display");
        nameKey.put("2025_NFL_Advanced_Receiving","name_display");
        nameKey.put("2025_NFL_Advanced_Defense","name_display");
        nameKey.put("2025_NFL_Fantasy_Rankings","player");
        nameKey.put("2025_Red_Zone_Passing_Stats","player");
        nameKey.put("2025_Red_Zone_Rushing_Stats","player");
        nameKey.put("2025_Red_Zone_Receiving_Stats","player");
    }

    public ArrayList<Object> getStatsById(int ID){

        ArrayList<Object> output = new ArrayList<Object>();

        output.add(passingStatsRepository.findById(ID));
        output.add(scrimmageStatsRepository.findById(ID));
        output.add(defenseStatsRepository.findById(ID));
        output.add(advancedPassingStatsRepository.findById(ID));
        output.add(advancedRushingStatsRepository.findById(ID));
        output.add(advancedReceivingStatsRepository.findById(ID));
        output.add(advancedDefenseStatsRepository.findById(ID));
        output.add(fantasyRankingsRepository.findById(ID));
        output.add(redZonePassingStatsRepository.findById(ID));
        output.add(redZoneRushingStatsRepository.findById(ID));
        output.add(redZoneReceivingStatsRepository.findById(ID));

        return output;
    }

    public ArrayList<Object> getStatsByName(String name){

        ArrayList<Object> output = new ArrayList<>();

        int ID = playerDatabaseService.getPlayerIDs().get(name);

        output.add(passingStatsRepository.findById(ID));
        output.add(scrimmageStatsRepository.findById(ID));
        output.add(defenseStatsRepository.findById(ID));
        output.add(advancedPassingStatsRepository.findById(ID));
        output.add(advancedRushingStatsRepository.findById(ID));
        output.add(advancedReceivingStatsRepository.findById(ID));
        output.add(advancedDefenseStatsRepository.findById(ID));
        output.add(fantasyRankingsRepository.findById(ID));
        output.add(redZonePassingStatsRepository.findById(ID));
        output.add(redZoneRushingStatsRepository.findById(ID));
        output.add(redZoneReceivingStatsRepository.findById(ID));

        return output;
    }

    private void updatePlayerStats(){
        logger.info("Updating player stats");

        // set up rate limiter
        RateLimiter rl = RateLimiter.create((20.0/60.0));

        rl.acquire();
        playerDatabaseService.updatePlayerDatabase();

        for(String url : Settings.allPFRSeasonStatsURL){
            try {
                rl.acquire();

                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36")
                        .timeout(1000)
                        .get();

                // grab title
                String title = doc.title().split("\\|")[0].trim().replaceAll("\\s+", "_");
                if (title == null ||  title.isEmpty()) {
                    logger.error("Title is null or empty");
                    throw new RuntimeException("Title is null or empty");
                }


                // load in table
                Element table = doc.selectFirst("table");
                if (table == null) {
                    logger.error("Table is null or empty");
                    throw new RuntimeException("Table not found URL: " + url);
                }

                // grab headers
                Elements header = table.select("thead tr:last-child th");
                if (header.isEmpty()) {
                    logger.error("Header is null or empty");
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

                    jsonMap.put(playerDatabaseService.getPlayerIDs().get(dataMap.get(nameKey.get(title)).toString().replaceAll("[^\\p{L}]", "").toLowerCase()), dataMap);

                    if(dataMap.get(nameKey.get(title)).toString() == null){
                        logger.error("No name in scraped row");
                        continue;
                    }

                    if(title.equals("2025_NFL_Passing")){
                        PassingStats passingStats = new PassingStats();

                        passingStats.setPlayerId(playerDatabaseService.getPlayerIDs().get(dataMap.get(nameKey.get(title)).toString().replaceAll("[^\\p{L}]", "").toLowerCase()));
                        passingStats.setPlayerName(dataMap.get(nameKey.get(title)).toString());
                        passingStats.setAge(Integer.parseInt(dataMap.get("age").toString()));
                        passingStats.setTeam(dataMap.get("team_name_abbr").toString());
                        passingStats.setPosition(dataMap.get("pos").toString());
                        passingStats.setGames(Integer.parseInt(dataMap.get("games").toString()));
                        passingStats.setGamesStarted(Integer.parseInt(dataMap.get("games_started").toString()));
                        passingStats.setRank(Integer.parseInt(dataMap.get("ranker").toString()));
                        passingStats.setRecord(dataMap.get("qb_rec").toString());
                        passingStats.setAttempts(Integer.parseInt(dataMap.get("pass_att").toString()));
                        passingStats.setCompletions(Integer.parseInt(dataMap.get("pass_cmp").toString()));
                        passingStats.setCompRate(Double.parseDouble(dataMap.get("pass_cmp_pct").toString()));
                        passingStats.setYards(Integer.parseInt(dataMap.get("pass_yds").toString()));
                        passingStats.setTouchDowns(Integer.parseInt(dataMap.get("pass_td").toString()));
                        passingStats.setTouchDownRate(Double.parseDouble(dataMap.get("pass_td_pct").toString()));
                        passingStats.setInterceptions(Integer.parseInt(dataMap.get("pass_int").toString()));
                        passingStats.setInterceptionRate(Double.parseDouble(dataMap.get("pass_int_pct").toString()));
                        passingStats.setFirstDowns(Integer.parseInt(dataMap.get("pass_first_down").toString()));
                        passingStats.setPassingSuccessRate(Double.parseDouble(dataMap.get("pass_success").toString()));
                        passingStats.setLongestPass(Integer.parseInt(dataMap.get("pass_long").toString()));
                        passingStats.setYardsPerAttempt(Integer.parseInt(dataMap.get("pass_yds_per_att").toString()));
                        passingStats.setAdjustedYardsPerAttempt(Integer.parseInt(dataMap.get("pass_adj_yds_per_att").toString()));
                        passingStats.setYardsPerCompletion(Integer.parseInt(dataMap.get("pass_yds_per_cmp").toString()));
                        passingStats.setYardsPerGame(Integer.parseInt(dataMap.get("pass_yds_per_g").toString()));
                        passingStats.setPasserRate(Double.parseDouble(dataMap.get("pass_rating").toString()));
                        passingStats.setQbr(Double.parseDouble(dataMap.get("qbr").toString()));
                        passingStats.setSacks(Integer.parseInt(dataMap.get("pass_sacked").toString()));
                        passingStats.setSackRate(Double.parseDouble(dataMap.get("pass_sacked_pct").toString()));
                        passingStats.setYardsLost(Integer.parseInt(dataMap.get("pass_sacked_yds").toString()));
                        passingStats.setNetYardsGainedPerAttempt(Integer.parseInt(dataMap.get("pass_net_yds_per_att").toString()));
                        passingStats.setAdjustedNetYardsPerAttempt(Integer.parseInt(dataMap.get("pass_adj_net_yds_per_att").toString()));
                        passingStats.setComebacksLed(Integer.parseInt(dataMap.get("comebacks").toString()));
                        passingStats.setGameWinningDrive(Integer.parseInt(dataMap.get("gwd").toString()));
                        passingStats.setAwards(dataMap.get("awards").toString());

                        passingStatsRepository.save(passingStats);
                    }
                    else if(title.equals("2025_NFL_Scrimmage_Stats")){
                        ScrimmageStats scrimmageStats = new ScrimmageStats();

                        scrimmageStats.setPlayerId(playerDatabaseService.getPlayerIDs().get(dataMap.get(nameKey.get(title)).toString().replaceAll("[^\\p{L}]", "").toLowerCase()));
                        scrimmageStats.setPlayerName(dataMap.get(nameKey.get(title)).toString());
                        scrimmageStats.setAge(Integer.parseInt(dataMap.get("age").toString()));
                        scrimmageStats.setTeam(dataMap.get("team_name_abbr").toString());
                        scrimmageStats.setPosition(dataMap.get("pos").toString());
                        scrimmageStats.setGames(Integer.parseInt(dataMap.get("games").toString()));
                        scrimmageStats.setGamesStarted(Integer.parseInt(dataMap.get("games_started").toString()));
                        scrimmageStats.setRank(Integer.parseInt(dataMap.get("ranker").toString()));
                        scrimmageStats.setRushAttempts(Integer.parseInt(dataMap.get("rush_att").toString()));
                        scrimmageStats.setRushYards(Integer.parseInt(dataMap.get("rush_yds").toString()));
                        scrimmageStats.setRushYardsPerAtt(Integer.parseInt(dataMap.get("rush_yds_per_att").toString()));
                        scrimmageStats.setRushYardsPerGame(Integer.parseInt(dataMap.get("rush_yds_per_g").toString()));
                        scrimmageStats.setLongestRush(Integer.parseInt(dataMap.get("rush_long").toString()));
                        scrimmageStats.setRushSuccessRate(Double.parseDouble(dataMap.get("rush_success").toString()));
                        scrimmageStats.setRushTouchDowns(Integer.parseInt(dataMap.get("rush_td").toString()));
                        scrimmageStats.setRushFirstDowns(Integer.parseInt(dataMap.get("rush_first_down").toString()));
                        scrimmageStats.setRushAttemptsPerGame(Integer.parseInt(dataMap.get("rush_att_per_g").toString()));
                        scrimmageStats.setFumbles(Integer.parseInt(dataMap.get("fumbles").toString()));
                        scrimmageStats.setTargets(Integer.parseInt(dataMap.get("targets").toString()));
                        scrimmageStats.setReceptions(Integer.parseInt(dataMap.get("rec").toString()));
                        scrimmageStats.setReceptionsPerGame(Integer.parseInt(dataMap.get("rec_per_g").toString()));
                        scrimmageStats.setCatchRate(Double.parseDouble(dataMap.get("catch_pct").toString()));
                        scrimmageStats.setReceptionSuccessRate(Double.parseDouble(dataMap.get("rec_success").toString()));
                        scrimmageStats.setRecYards(Integer.parseInt(dataMap.get("rec_yds").toString()));
                        scrimmageStats.setReceivingYardsPerGame(Integer.parseInt(dataMap.get("rec_yds_per_g").toString()));
                        scrimmageStats.setReceivingYardsPerReception(Integer.parseInt(dataMap.get("rec_yds_per_rec").toString()));
                        scrimmageStats.setReceivingFirstDowns(Integer.parseInt(dataMap.get("rec_first_down").toString()));
                        scrimmageStats.setYardsFromScrimmage(Integer.parseInt(dataMap.get("yds_from_scrimmage").toString()));
                        scrimmageStats.setReceivingYardsPerTarget(Integer.parseInt(dataMap.get("rec_yds_per_tgt").toString()));
                        scrimmageStats.setYardsPerTouch(Integer.parseInt(dataMap.get("yds_per_touch").toString()));
                        scrimmageStats.setReceivingTouchDowns(Integer.parseInt(dataMap.get("rec_td").toString()));
                        scrimmageStats.setRushReceiveTouchDowns(Integer.parseInt(dataMap.get("rush_receive_td").toString()));
                        scrimmageStats.setLongestReception(Integer.parseInt(dataMap.get("rec_long").toString()));
                        scrimmageStats.setTouches(Integer.parseInt(dataMap.get("touches").toString()));
                        scrimmageStats.setAwards(dataMap.get("awards").toString());

                        scrimmageStatsRepository.save(scrimmageStats);
                    }
                    else if(title.equals("2025_NFL_Defense")){
                        DefenseStats defenseStats = new DefenseStats();

                        defenseStats.setPlayerId(playerDatabaseService.getPlayerIDs().get(dataMap.get(nameKey.get(title)).toString().replaceAll("[^\\p{L}]", "").toLowerCase()));
                        defenseStats.setPlayerName(dataMap.get(nameKey.get(title)).toString());
                        defenseStats.setAge(Integer.parseInt(dataMap.get("age").toString()));
                        defenseStats.setTeam(dataMap.get("team_name_abbr").toString());
                        defenseStats.setPosition(dataMap.get("pos").toString());
                        defenseStats.setGames(Integer.parseInt(dataMap.get("games").toString()));
                        defenseStats.setGamesStarted(Integer.parseInt(dataMap.get("games_started").toString()));
                        defenseStats.setRank(Integer.parseInt(dataMap.get("ranker").toString()));
                        defenseStats.setSoloTackles(Integer.parseInt(dataMap.get("tackles_solo").toString()));
                        defenseStats.setTackleAssists(Integer.parseInt(dataMap.get("tackles_assists").toString()));
                        defenseStats.setComboTackles(Integer.parseInt(dataMap.get("tackles_combined").toString()));
                        defenseStats.setTacklesForLoss(Integer.parseInt(dataMap.get("tackles_loss").toString()));
                        defenseStats.setSacks(Integer.parseInt(dataMap.get("sacks").toString()));
                        defenseStats.setQuarterBackHits(Integer.parseInt(dataMap.get("qb_hits").toString()));
                        defenseStats.setFumbles(Integer.parseInt(dataMap.get("fumbles").toString()));
                        defenseStats.setFumblesForced(Integer.parseInt(dataMap.get("fumbles_forced").toString()));
                        defenseStats.setFumblesRecovered(Integer.parseInt(dataMap.get("fumbles_rec").toString()));
                        defenseStats.setFumbleRecYards(Integer.parseInt(dataMap.get("fumbles_rec_yds").toString()));
                        defenseStats.setFumbleRecoveryTouchDowns(Integer.parseInt(dataMap.get("fumbles_rec_td").toString()));
                        defenseStats.setSafety(Integer.parseInt(dataMap.get("safety_md").toString()));
                        defenseStats.setInterceptions(Integer.parseInt(dataMap.get("def_int").toString()));
                        defenseStats.setPassesDefended(Integer.parseInt(dataMap.get("pass_defended").toString()));
                        defenseStats.setInterceptionReturnYards(Integer.parseInt(dataMap.get("def_int_yds").toString()));
                        defenseStats.setLongestInterceptionReturn(Integer.parseInt(dataMap.get("def_int_long").toString()));
                        defenseStats.setInterceptionReturnTouchDown(Integer.parseInt(dataMap.get("def_int_td").toString()));
                        defenseStats.setAwards(dataMap.get("awards").toString());

                        defenseStatsRepository.save(defenseStats);
                    }
                    else if(title.equals("2025_NFL_Advanced_Passing")){
                        AdvancedPassingStats advancedPassingStats = new AdvancedPassingStats();

                        advancedPassingStats.setPlayerId(playerDatabaseService.getPlayerIDs().get(dataMap.get(nameKey.get(title)).toString().replaceAll("[^\\p{L}]", "").toLowerCase()));
                        advancedPassingStats.setPlayerName(dataMap.get(nameKey.get(title)).toString());
                        advancedPassingStats.setAge(Integer.parseInt(dataMap.get("age").toString()));
                        advancedPassingStats.setTeam(dataMap.get("team_name_abbr").toString());
                        advancedPassingStats.setPosition(dataMap.get("pos").toString());
                        advancedPassingStats.setGames(Integer.parseInt(dataMap.get("games").toString()));
                        advancedPassingStats.setGamesStarted(Integer.parseInt(dataMap.get("games_started").toString()));
                        advancedPassingStats.setRank(Integer.parseInt(dataMap.get("ranker").toString()));
                        advancedPassingStats.setPassAttempts(Integer.parseInt(dataMap.get("pass_att").toString()));
                        advancedPassingStats.setPassComps(Integer.parseInt(dataMap.get("pass_cmp").toString()));
                        advancedPassingStats.setYardsAfterCatch(Integer.parseInt(dataMap.get("pass_yac").toString()));
                        advancedPassingStats.setPassDrops(Integer.parseInt(dataMap.get("pass_drops").toString()));
                        advancedPassingStats.setPassDropPercent(Integer.parseInt(dataMap.get("pass_drop_pct").toString()));
                        advancedPassingStats.setSpikes(Integer.parseInt(dataMap.get("pass_spikes").toString()));
                        advancedPassingStats.setPassPlayAction(Integer.parseInt(dataMap.get("pass_play_action").toString()));
                        advancedPassingStats.setPlayActionPassYards(Integer.parseInt(dataMap.get("pass_play_action_pass_yds").toString()));
                        advancedPassingStats.setPassThrowaways(Integer.parseInt(dataMap.get("pass_throwaways").toString()));
                        advancedPassingStats.setBattedPasses(Integer.parseInt(dataMap.get("pass_batted_passes").toString()));
                        advancedPassingStats.setIntendedAirYards(Integer.parseInt(dataMap.get("pass_air_yds").toString()));
                        advancedPassingStats.setPassesOnTarget(Integer.parseInt(dataMap.get("pass_on_target").toString()));
                        advancedPassingStats.setPassAirYardsPerCompletion(Integer.parseInt(dataMap.get("pass_air_yds_per_cmp").toString()));
                        advancedPassingStats.setPassTargetYardsPerAttempt(Integer.parseInt(dataMap.get("pass_tgt_yds_per_att").toString()));
                        advancedPassingStats.setPoorPasses(Integer.parseInt(dataMap.get("pass_poor_throws").toString()));
                        advancedPassingStats.setPassRPO(Integer.parseInt(dataMap.get("pass_rpo").toString()));
                        advancedPassingStats.setPassRPOPassAttempts(Integer.parseInt(dataMap.get("pass_rpo_pass_att").toString()));
                        advancedPassingStats.setYardsRPO(Integer.parseInt(dataMap.get("pass_rpo_yds").toString()));
                        advancedPassingStats.setPassYardsRPO(Integer.parseInt(dataMap.get("pass_rpo_pass_yds").toString()));
                        advancedPassingStats.setRushYardsRPO(Integer.parseInt(dataMap.get("pass_rpo_rush_yds").toString()));
                        advancedPassingStats.setRushAttemptsRPO(Integer.parseInt(dataMap.get("pass_rpo_rush_att").toString()));
                        advancedPassingStats.setPassesHurried(Integer.parseInt(dataMap.get("pass_hurried").toString()));
                        advancedPassingStats.setPassesHit(Integer.parseInt(dataMap.get("pass_hits").toString()));
                        advancedPassingStats.setCompletedAirYards(Integer.parseInt(dataMap.get("pass_air_yds").toString()));
                        advancedPassingStats.setNumScrambles(Integer.parseInt(dataMap.get("rush_scrambles").toString()));
                        advancedPassingStats.setPocketTime(Integer.parseInt(dataMap.get("pocket_time").toString()));
                        advancedPassingStats.setPoorPassThrowRate(Integer.parseInt(dataMap.get("pass_poor_throw_pct").toString()));
                        advancedPassingStats.setTimesPressured(Integer.parseInt(dataMap.get("pass_pressured").toString()));
                        advancedPassingStats.setTimesBlitzed(Integer.parseInt(dataMap.get("pass_blitzed").toString()));
                        advancedPassingStats.setPressuredPercent(Integer.parseInt(dataMap.get("pass_pressured_pct").toString()));
                        advancedPassingStats.setPassYardsAfterCatchPerCompletion(Integer.parseInt(dataMap.get("pass_yac_per_cmp").toString()));
                        advancedPassingStats.setScrambleYardsPerAttempt(Integer.parseInt(dataMap.get("rush_scrambles_yds_per_att").toString()));
                        advancedPassingStats.setPassAirYardsPerAttempt(Integer.parseInt(dataMap.get("pass_air_yds_per_att").toString()));
                        advancedPassingStats.setOnTargetPassPercent(Integer.parseInt(dataMap.get("pass_on_target_pct").toString()));
                        advancedPassingStats.setAwards(dataMap.get("awards").toString());

                        advancedPassingStatsRepository.save(advancedPassingStats);
                    }
                    else if(title.equals("2025_NFL_Advanced_Rushing")){
                        AdvancedRushingStats advancedRushingStats = new AdvancedRushingStats();

                        advancedRushingStats.setPlayerId(playerDatabaseService.getPlayerIDs().get(dataMap.get(nameKey.get(title)).toString().replaceAll("[^\\p{L}]", "").toLowerCase()));
                        advancedRushingStats.setPlayerName(dataMap.get(nameKey.get(title)).toString());
                        advancedRushingStats.setAge(Integer.parseInt(dataMap.get("age").toString()));
                        advancedRushingStats.setTeam(dataMap.get("team_name_abbr").toString());
                        advancedRushingStats.setPosition(dataMap.get("pos").toString());
                        advancedRushingStats.setGames(Integer.parseInt(dataMap.get("games").toString()));
                        advancedRushingStats.setGamesStarted(Integer.parseInt(dataMap.get("games_started").toString()));
                        advancedRushingStats.setRank(Integer.parseInt(dataMap.get("ranker").toString()));
                        advancedRushingStats.setRushAttempts(Integer.parseInt(dataMap.get("rush_att").toString()));
                        advancedRushingStats.setRushYards(Integer.parseInt(dataMap.get("rush_yds").toString()));
                        advancedRushingStats.setRushYardsAfterContact(Integer.parseInt(dataMap.get("rush_yac").toString()));
                        advancedRushingStats.setRushYardsAfterContactPerRush(Integer.parseInt(dataMap.get("rush_yac_per_rush").toString()));
                        advancedRushingStats.setRushYardsBeforeContact(Integer.parseInt(dataMap.get("rush_yds_before_contact").toString()));
                        advancedRushingStats.setRushYardsBeforeContactPerRush(Double.parseDouble(dataMap.get("rush_yds_bc_per_rush").toString()));
                        advancedRushingStats.setRushBrokenTackles(Integer.parseInt(dataMap.get("rush_broken_tackles").toString()));
                        advancedRushingStats.setRushBrokenTacklesPerRush(Integer.parseInt(dataMap.get("rush_broken_tackles_per_rush").toString()));
                        advancedRushingStats.setRushFirstDowns(Integer.parseInt(dataMap.get("rush_first_down").toString()));
                        advancedRushingStats.setAwards(dataMap.get("awards").toString());

                        advancedRushingStatsRepository.save(advancedRushingStats);
                    }
                    else if(title.equals("2025_NFL_Advanced_Receiving")){
                        AdvancedReceivingStats advancedReceivingStats = new AdvancedReceivingStats();

                        advancedReceivingStats.setPlayerId(playerDatabaseService.getPlayerIDs().get(dataMap.get(nameKey.get(title)).toString().replaceAll("[^\\p{L}]", "").toLowerCase()));
                        advancedReceivingStats.setPlayerName(dataMap.get(nameKey.get(title)).toString());
                        advancedReceivingStats.setAge(Integer.parseInt(dataMap.get("age").toString()));
                        advancedReceivingStats.setTeam(dataMap.get("team_name_abbr").toString());
                        advancedReceivingStats.setPosition(dataMap.get("pos").toString());
                        advancedReceivingStats.setGames(Integer.parseInt(dataMap.get("games").toString()));
                        advancedReceivingStats.setGamesStarted(Integer.parseInt(dataMap.get("games_started").toString()));
                        advancedReceivingStats.setRank(Integer.parseInt(dataMap.get("ranker").toString()));
                        advancedReceivingStats.setTargets(Integer.parseInt(dataMap.get("targets").toString()));
                        advancedReceivingStats.setReceptions(Integer.parseInt(dataMap.get("rec").toString()));
                        advancedReceivingStats.setRecYards(Integer.parseInt(dataMap.get("rec_yds").toString()));
                        advancedReceivingStats.setDrops(Integer.parseInt(dataMap.get("rec_drops").toString()));
                        advancedReceivingStats.setDropRate(Double.parseDouble(dataMap.get("rec_drop_pct").toString()));
                        advancedReceivingStats.setYardsAfterCatch(Integer.parseInt(dataMap.get("rec_yac").toString()));
                        advancedReceivingStats.setYardsAfterCatchPerReception(Integer.parseInt(dataMap.get("rec_yac_per_rec").toString()));
                        advancedReceivingStats.setAirYards(Integer.parseInt(dataMap.get("rec_air_yds").toString()));
                        advancedReceivingStats.setAirYardsPerCatch(Integer.parseInt(dataMap.get("rec_air_yds_per_rec").toString()));
                        advancedReceivingStats.setReceivingBrokenTackles(Integer.parseInt(dataMap.get("rec_broken_tackles").toString()));
                        advancedReceivingStats.setReceptionsPerBrokenTackle(Integer.parseInt(dataMap.get("rec_broken_tackles_per_rec").toString()));
                        advancedReceivingStats.setAverageDepthOfTarget(Integer.parseInt(dataMap.get("rec_adot").toString()));
                        advancedReceivingStats.setInterceptionsWhenTargeted(Integer.parseInt(dataMap.get("rec_target_int").toString()));
                        advancedReceivingStats.setReceivingFirstDowns(Integer.parseInt(dataMap.get("rec_first_down").toString()));
                        advancedReceivingStats.setReceivingPasserRating(Integer.parseInt(dataMap.get("rec_pass_rating").toString()));
                        advancedReceivingStats.setAwards(dataMap.get("awards").toString());

                        advancedReceivingStatsRepository.save(advancedReceivingStats);
                    }
                    else if(title.equals("2025_NFL_Advanced_Defense")){
                        AdvancedDefenseStats advancedDefenseStats = new AdvancedDefenseStats();

                        advancedDefenseStats.setPlayerId(playerDatabaseService.getPlayerIDs().get(dataMap.get(nameKey.get(title)).toString().replaceAll("[^\\p{L}]", "").toLowerCase()));
                        advancedDefenseStats.setPlayerName(dataMap.get(nameKey.get(title)).toString());
                        advancedDefenseStats.setAge(Integer.parseInt(dataMap.get("age").toString()));
                        advancedDefenseStats.setTeam(dataMap.get("team_name_abbr").toString());
                        advancedDefenseStats.setPosition(dataMap.get("pos").toString());
                        advancedDefenseStats.setGames(Integer.parseInt(dataMap.get("games").toString()));
                        advancedDefenseStats.setGamesStarted(Integer.parseInt(dataMap.get("games_started").toString()));
                        advancedDefenseStats.setRank(Integer.parseInt(dataMap.get("ranker").toString()));
                        advancedDefenseStats.setSacks(Integer.parseInt(dataMap.get("sacks").toString()));
                        advancedDefenseStats.setBlitz(Integer.parseInt(dataMap.get("blitzes").toString()));
                        advancedDefenseStats.setInterceptions(Integer.parseInt(dataMap.get("def_int").toString()));
                        advancedDefenseStats.setPressures(Integer.parseInt(dataMap.get("pressures").toString()));
                        advancedDefenseStats.setQuarterBackHurries(Integer.parseInt(dataMap.get("qb_hurry").toString()));
                        advancedDefenseStats.setTacklesMissed(Integer.parseInt(dataMap.get("tackles_missed").toString()));
                        advancedDefenseStats.setMissedTackleRate(Integer.parseInt(dataMap.get("tackles_missed_pct").toString()));
                        advancedDefenseStats.setCombinedTackles(Integer.parseInt(dataMap.get("tackles_combined").toString()));
                        advancedDefenseStats.setQuarterBackKnockDowns(Integer.parseInt(dataMap.get("qb_knockdown").toString()));
                        advancedDefenseStats.setBattedPasses(Integer.parseInt(dataMap.get("def_batted_passes").toString()));
                        advancedDefenseStats.setCompletionsAllowed(Integer.parseInt(dataMap.get("def_cmp").toString()));
                        advancedDefenseStats.setDefensiveTargets(Integer.parseInt(dataMap.get("def_targets").toString()));
                        advancedDefenseStats.setCompletionRateAllowed(Integer.parseInt(dataMap.get("def_cmp_pct").toString()));
                        advancedDefenseStats.setYardsAllowedPerTarget(Integer.parseInt(dataMap.get("def_yds_per_target").toString()));
                        advancedDefenseStats.setYardsAllowedPerAttempt(Integer.parseInt(dataMap.get("def_tgt_yds_per_att").toString()));
                        advancedDefenseStats.setYardsAllowedPerCompletion(Integer.parseInt(dataMap.get("def_yds_per_cmp").toString()));
                        advancedDefenseStats.setAirYardsAllowed(Integer.parseInt(dataMap.get("def_air_yds").toString()));
                        advancedDefenseStats.setYardsAfterCatchAllowed(Integer.parseInt(dataMap.get("def_yac").toString()));
                        advancedDefenseStats.setTimesTargeted(Integer.parseInt(dataMap.get("def_targets").toString()));
                        advancedDefenseStats.setTouchDownsAllowed(Integer.parseInt(dataMap.get("def_cmp_td").toString()));
                        advancedDefenseStats.setPasserRatingAllowed(Integer.parseInt(dataMap.get("def_pass_rating").toString()));
                        advancedDefenseStats.setYardsAllowedPerCompletion(Integer.parseInt(dataMap.get("def_cmp_yds").toString()));
                        advancedDefenseStats.setAwards(dataMap.get("awards").toString());

                        advancedDefenseStatsRepository.save(advancedDefenseStats);
                    }
                    else if(title.equals("2025_NFL_Fantasy_Rankings")){
                        FantasyRankings fantasyRankings = new FantasyRankings();

                        fantasyRankings.setPlayerId(playerDatabaseService.getPlayerIDs().get(dataMap.get(nameKey.get(title)).toString().replaceAll("[^\\p{L}]", "").toLowerCase()));
                        fantasyRankings.setPlayerName(dataMap.get(nameKey.get(title)).toString());
                        fantasyRankings.setAge(Integer.parseInt(dataMap.get("age").toString()));
                        fantasyRankings.setTeam(dataMap.get("team_name_abbr").toString());
                        fantasyRankings.setPosition(dataMap.get("pos").toString());
                        fantasyRankings.setGames(Integer.parseInt(dataMap.get("games").toString()));
                        fantasyRankings.setGamesStarted(Integer.parseInt(dataMap.get("games_started").toString()));
                        fantasyRankings.setRank(Integer.parseInt(dataMap.get("ranker").toString()));
                        fantasyRankings.setFantasyRankOverall(Integer.parseInt(dataMap.get("fantasy_rank_overall").toString())); // fantasyRankOverall
                        fantasyRankings.setFantasyPositionRank(Integer.parseInt(dataMap.get("fantasy_rank_pos").toString()));
                        fantasyRankings.setFantasyPointsPPR(Double.parseDouble(dataMap.get("fantasy_points_ppr").toString())); // fantasyPointsPPR
                        fantasyRankings.setFantasyPoints(Double.parseDouble(dataMap.get("fantasy_points").toString())); // fantasyPoints
                        fantasyRankings.setFanduelPoints(Double.parseDouble(dataMap.get("fanduel_points").toString()));
                        fantasyRankings.setDraftKingsPoints(Double.parseDouble(dataMap.get("draftkings_points").toString()));
                        fantasyRankings.setVbd(Integer.parseInt(dataMap.get("vbd").toString()));
                        fantasyRankings.setPassAttempts(Integer.parseInt(dataMap.get("pass_att").toString()));
                        fantasyRankings.setPassCompletions(Integer.parseInt(dataMap.get("pass_cmp").toString()));
                        fantasyRankings.setPassYards(Integer.parseInt(dataMap.get("pass_yds").toString()));
                        fantasyRankings.setPassTouchDowns(Integer.parseInt(dataMap.get("pass_td").toString()));
                        fantasyRankings.setPassInterceptions(Integer.parseInt(dataMap.get("pass_int").toString()));
                        fantasyRankings.setTwoPointConversionPasses(Integer.parseInt(dataMap.get("two_pt_pass").toString()));
                        fantasyRankings.setRushAttempts(Integer.parseInt(dataMap.get("rush_att").toString()));
                        fantasyRankings.setRushYards(Integer.parseInt(dataMap.get("rush_yds").toString()));
                        fantasyRankings.setRushYardsPerAttempt(Integer.parseInt(dataMap.get("rush_yds_per_att").toString()));
                        fantasyRankings.setRushTouchDowns(Integer.parseInt(dataMap.get("rush_td").toString()));
                        fantasyRankings.setFumbles(Integer.parseInt(dataMap.get("fumbles").toString()));
                        fantasyRankings.setFumblesLost(Integer.parseInt(dataMap.get("fumbles_lost").toString()));
                        fantasyRankings.setTargets(Integer.parseInt(dataMap.get("targets").toString()));
                        fantasyRankings.setReceptions(Integer.parseInt(dataMap.get("rec").toString()));
                        fantasyRankings.setRecYards(Integer.parseInt(dataMap.get("rec_yds").toString()));
                        fantasyRankings.setYardsPerReception(Integer.parseInt(dataMap.get("rec_yds_per_rec").toString()));
                        fantasyRankings.setReceivingTouchDowns(Integer.parseInt(dataMap.get("rec_td").toString()));
                        fantasyRankings.setTotalTouchDowns(Integer.parseInt(dataMap.get("all_td").toString()));
                        fantasyRankings.setTwoPointMade(Integer.parseInt(dataMap.get("two_pt_md").toString()));

                        fantasyRankingsRepository.save(fantasyRankings);
                    }
                    else if(title.equals("2025_Red_Zone_Passing_Stats")){
                        RedZonePassingStats redZonePassingStats = new RedZonePassingStats();

                        redZonePassingStats.setPlayerId(playerDatabaseService.getPlayerIDs().get(dataMap.get(nameKey.get(title)).toString().replaceAll("[^\\p{L}]", "").toLowerCase()));
                        redZonePassingStats.setPlayerName(dataMap.get(nameKey.get(title)).toString());
                        redZonePassingStats.setTeam(dataMap.get("team_name_abbr").toString());
                        redZonePassingStats.setPassAttempts(Integer.parseInt(dataMap.get("pass_att").toString()));
                        redZonePassingStats.setPassCompletions(Integer.parseInt(dataMap.get("pass_cmp").toString()));
                        redZonePassingStats.setPassCompletionRate(Double.parseDouble(dataMap.get("pass_cmp_perc").toString()));
                        redZonePassingStats.setPassYards(Integer.parseInt(dataMap.get("pass_yds").toString()));
                        redZonePassingStats.setPassTouchDowns(Integer.parseInt(dataMap.get("pass_td").toString()));
                        redZonePassingStats.setPassInterceptions(Integer.parseInt(dataMap.get("pass_int").toString()));
                        redZonePassingStats.setPassAttemptsInTen(Integer.parseInt(dataMap.get("pass_att_in_10").toString()));
                        redZonePassingStats.setPassCompletionRateInTen(Double.parseDouble(dataMap.get("pass_cmp_in_10").toString()));
                        redZonePassingStats.setPassYardsInTen(Integer.parseInt(dataMap.get("pass_yds_in_10").toString()));
                        redZonePassingStats.setPassTouchDownsInTen(Integer.parseInt(dataMap.get("pass_td_in_10").toString()));
                        redZonePassingStats.setPassInterceptionsInTen(Integer.parseInt(dataMap.get("pass_int_in_10").toString()));
                        redZonePassingStats.setPassCompletionRateInTen(Double.parseDouble(dataMap.get("pass_cmp_perc_in_10").toString()));

                        redZonePassingStatsRepository.save(redZonePassingStats);
                    }
                    else if(title.equals("2025_Red_Zone_Rushing_Stats")){
                        RedZoneRushingStats redZoneRushingStats = new RedZoneRushingStats();

                        redZoneRushingStats.setPlayerId(playerDatabaseService.getPlayerIDs().get(dataMap.get(nameKey.get(title)).toString().replaceAll("[^\\p{L}]", "").toLowerCase()));
                        redZoneRushingStats.setPlayerName(dataMap.get(nameKey.get(title)).toString());
                        redZoneRushingStats.setTeam(dataMap.get("team_name_abbr").toString());
                        redZoneRushingStats.setRushAttempts(Integer.parseInt(dataMap.get("rush_att").toString()));
                        redZoneRushingStats.setRushYards(Integer.parseInt(dataMap.get("rush_yds").toString()));
                        redZoneRushingStats.setRushTouchDowns(Integer.parseInt(dataMap.get("rush_td").toString()));
                        redZoneRushingStats.setPercentOfTeamRushes(Double.parseDouble(dataMap.get("rush_att_pct").toString()));
                        redZoneRushingStats.setRushAttemptsInTen(Integer.parseInt(dataMap.get("rush_att_in_10").toString()));
                        redZoneRushingStats.setRushYardsInTen(Integer.parseInt(dataMap.get("rush_yds_in_10").toString()));
                        redZoneRushingStats.setRushTouchDownsInTen(Integer.parseInt(dataMap.get("rush_td_in_10").toString()));
                        redZoneRushingStats.setPercentOfTeamRushesInTen(Double.parseDouble(dataMap.get("rush_att_in_10_pct").toString()));
                        redZoneRushingStats.setRushAttemptsInFive(Integer.parseInt(dataMap.get("rush_att_in_5").toString()));
                        redZoneRushingStats.setRushYardsInFive(Integer.parseInt(dataMap.get("rush_yds_in_5").toString()));
                        redZoneRushingStats.setRushTouchDownsInFive(Integer.parseInt(dataMap.get("rush_td_in_5").toString()));
                        redZoneRushingStats.setPercentOfTeamRushesInFive(Double.parseDouble(dataMap.get("rush_att_in_5_pct").toString()));

                        redZoneRushingStatsRepository.save(redZoneRushingStats);
                    }
                    else if(title.equals("2025_Red_Zone_Receiving_Stats")){
                        RedZoneReceivingStats redZoneReceivingStats = new RedZoneReceivingStats();

                        redZoneReceivingStats.setPlayerId(playerDatabaseService.getPlayerIDs().get(dataMap.get(nameKey.get(title)).toString().replaceAll("[^\\p{L}]", "").toLowerCase()));
                        redZoneReceivingStats.setPlayerName(dataMap.get(nameKey.get(title)).toString());
                        redZoneReceivingStats.setTeam(dataMap.get("team_name_abbr").toString());
                        redZoneReceivingStats.setTargets(Integer.parseInt(dataMap.get("targets").toString()));
                        redZoneReceivingStats.setReceptions(Integer.parseInt(dataMap.get("rec").toString()));
                        redZoneReceivingStats.setRecYards(Integer.parseInt(dataMap.get("rec_yds").toString()));
                        redZoneReceivingStats.setRecTouchDowns(Integer.parseInt(dataMap.get("rec_td").toString()));
                        redZoneReceivingStats.setCatchRate(Double.parseDouble(dataMap.get("catch_pct").toString()));
                        redZoneReceivingStats.setPercentOfTeamTargets(Double.parseDouble(dataMap.get("targets_pct").toString()));
                        redZoneReceivingStats.setTargetsInTen(Integer.parseInt(dataMap.get("targets_in_10").toString()));
                        redZoneReceivingStats.setReceptionsInTen(Integer.parseInt(dataMap.get("rec_in_10").toString()));
                        redZoneReceivingStats.setReceivingYardsInTen(Integer.parseInt(dataMap.get("rec_yds_in_10").toString()));
                        redZoneReceivingStats.setReceivingTouchDownsInTen(Integer.parseInt(dataMap.get("rec_td_in_10").toString()));
                        redZoneReceivingStats.setCatchRateInTen(Double.parseDouble(dataMap.get("catch_pct_in_10").toString()));
                        redZoneReceivingStats.setPercentOfTeamTargetsInTen(Double.parseDouble(dataMap.get("targets_in_10_pct").toString()));

                        redZoneReceivingStatsRepository.save(redZoneReceivingStats);

                    }
                    else{
                        logger.error("Invalid title: " + title);
                        throw new RuntimeException("Unknown title: " + title);
                    }

                }// end of row for loop

            } catch (Exception e){

            }
        }// end url for loop
    }// end of method
}
