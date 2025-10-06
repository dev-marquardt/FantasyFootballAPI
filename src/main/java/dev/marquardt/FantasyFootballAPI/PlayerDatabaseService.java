package dev.marquardt.FantasyFootballAPI;

// imports for logger and  Getter
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import databases
import dev.marquardt.FantasyFootballAPI.database.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

// imports to maps
import java.util.HashMap;
import java.util.Map;

@Service
public class PlayerDatabaseService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Getter
    private static final Map<String, Integer> playerIDs = new HashMap<String, Integer>();

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PlayerRepository playerRepository;

    public void updatePlayerDatabase(){
        logger.info("Started API Player Updating");

        try {
            String json = restTemplate.getForObject(Settings.sleeperURL, String.class);

            JSONObject jsonObject = new JSONObject(json);

            int playerId = 0;

            for (String key : jsonObject.keySet()) {
                if (key.matches("//d+")) {
                    playerId++;
                    JSONObject playerData = new JSONObject(key);

                    String searchFullName = playerData.optString("search_full_name", null);
                    if (searchFullName == null) {
                        logger.error("Null player name");

                        continue;
                    }

                    Player player = new Player();

                    player.setPlayerId(playerId);
                    player.setFirstName(playerData.optString("first_name", "N/A")); // firstName
                    player.setBirthState(playerData.optString("birth_state", null)); // birthState
                    player.setDepthChartPosition(playerData.optString("depth_chart_position", null)); // depthChartPosition
                    player.setTeam(playerData.optString("team", null)); // team (no change)
                    player.setAge(playerData.optInt("age", 0)); // age (no change)
                    player.setActive(playerData.optBoolean("active", false)); // active (no change)
                    player.setDepthChartOrder(playerData.optInt("depth_chart_order", 0)); // depthChartOrder
                    player.setWeight(playerData.optInt("weight", 0)); // weight (no change)
                    player.setCollege(playerData.optString("college", null)); // college (no change)
                    player.setBirthCity(playerData.optString("birth_city", null)); // birthCity
                    player.setInjuryNotes(playerData.optString("injury_notes", null)); // injuryNotes
                    player.setBirthDate(playerData.optString("birth_date", null)); // birthDate (assuming snake_case; adjust if "birthDate")
                    player.setStatus(playerData.optString("status", null)); // status (no change)
                    player.setSport(playerData.optString("sport", null)); // sport (no change)
                    player.setNewsUpdated(playerData.optLong("news_updated", 0L)); // newsUpdated
                    player.setHeight(playerData.optString("height", null)); // height (no change)
                    player.setBirthCountry(playerData.optString("birth_country", null)); // birthCountry
                    player.setYearsExp(playerData.optInt("years_exp", 0)); // yearsExp
                    player.setSearchFirstName(playerData.optString("search_first_name", null)); // searchFirstName
                    player.setNumber(playerData.optInt("number", 0)); // number (no change)
                    player.setInjuryBodyPart(playerData.optString("injury_body_part", null)); // injuryBodyPart
                    player.setPracticeDesc(playerData.optString("practice_desc", null)); // practiceDesc
                    player.setPracticePart(playerData.optString("practice_part", null)); // practicePart
                    player.setInjuryStartDate(playerData.optString("injury_start_date", null)); // injuryStartDate
                    player.setSearchRank(playerData.optInt("search_rank", 0)); // searchRank
                    player.setEspnId(playerData.optInt("espn_id", 0)); // espnId
                    player.setSportRadarId(playerData.optString("sports_radar_id", null)); // sportsRadarId
                    player.setYahooId(playerData.optInt("yahoo_id", 0)); // yahooId
                    player.setPosition(playerData.optString("position", null)); // position (no change)
                    player.setHighSchool(playerData.optString("high_school", null)); // highSchool
                    player.setSearchFullName(playerData.optString("search_full_name", null)); // searchFullName
                    player.setSearchLastName(playerData.optString("search_last_name", null)); // searchLastName
                    player.setLastName(playerData.optString("last_name", "N/A")); // lastName
                    player.setFullName(playerData.optString("full_name", "N/A")); // fullName
                    player.setInjuryStatus(playerData.optString("injury_status", null)); // injuryStatus

                    playerRepository.save(player);
                    playerIDs.put(player.getSearchFullName(),  player.getPlayerId());

                    logger.info("Saved player {} | ID: {}", player.getSearchFullName(),  player.getPlayerId());
                } // end if statement
            } // end for loop
        } catch (Exception e){
            logger.error("Failed to update player database", e);
            throw new RuntimeException("Failed to update player database", e);
        }
    } // end method
} // end class
