package dev.marquardt.FantasyFootballAPI;

import javax.xml.crypto.Data;
import java.util.Properties;

public class Settings {


    private static final Properties properties;

    protected static final String dbURL;

    protected static final String dbUser;

    protected static final String dbPass;

    protected static final String sleeperURL;

    protected static final String[] allPFRSeasonStatsURL;

    protected static final String currSeason;

    static {
        try {
            // Use the class loader from the Settings class itself for reliability
            var input = Settings.class.getClassLoader().getResourceAsStream("application.properties");

            if (input == null) {
                // If the file isn't found, throw a clear error
                throw new RuntimeException("Unable to find application.properties on the classpath");
            }

            // Initialize the properties object before using it
            properties = new Properties();
            properties.load(input);

        } catch (Exception e) {
            // If any other error occurs, log it and crash the application
            // This makes debugging much easier than failing silently
            throw new RuntimeException("Failed to load application.properties", e);
        }

        // Now, safely get the properties
        dbURL = properties.getProperty("spring.datasource.url");
        dbUser = properties.getProperty("spring.datasource.username");
        dbPass = properties.getProperty("spring.datasource.password");
        sleeperURL = properties.getProperty("sleeper.url");
        currSeason = properties.getProperty("nfl.currentseason");

        // This array initialization can now proceed without errors
        allPFRSeasonStatsURL = new String[]{
                String.format(properties.getProperty("pfr.seasonpassing.url"), currSeason),
                String.format(properties.getProperty("pfr.seasonscrimmage.url"), currSeason),
                String.format(properties.getProperty("pfr.seasondefense.url"), currSeason),
                String.format(properties.getProperty("pfr.advancedseasonpassing.url"), currSeason),
                String.format(properties.getProperty("pfr.advancedseasonrushing.url"), currSeason),
                String.format(properties.getProperty("pfr.advancedseasonreceiving.url"), currSeason),
                String.format(properties.getProperty("pfr.advancedseasondefense.url"), currSeason),
                String.format(properties.getProperty("pfr.fantasyplayerranks.url"), currSeason),
                String.format(properties.getProperty("pfr.redzonepassing.url"), currSeason),
                String.format(properties.getProperty("pfr.redzonerushing.url"), currSeason),
                String.format(properties.getProperty("pfr.redzonereceiving.url"), currSeason)
        };
    }
}
