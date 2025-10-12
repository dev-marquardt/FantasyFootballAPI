package dev.marquardt.FantasyFootballAPI;

import dev.marquardt.FantasyFootballAPI.database.Player;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class FantasyFootballApiApplication {

	public static void main(String[] args) {

        SpringApplication.run(FantasyFootballApiApplication.class, args);
	}

    // allows for command line testing
    @Bean
    @Profile("!test")
    CommandLineRunner run(PlayerDatabaseService playerService, PlayerStatsService statsService, PlayerStatsService playerStatsService) {
        return args -> {
            System.out.println("Starting FantasyFootballApiApplication in CMD line");

            System.out.println("Updating Player Databases...");
            playerService.updatePlayerDatabase();
            System.out.println("Player database has been updated.");

            System.out.println("Updating Stats Databases...");
            playerStatsService.updatePlayerStats();
            System.out.println("Stats database has been updated.");


            Optional<Player> player = null;
            //get player by id test
            System.out.println("Getting player by ID 3400");
            int id = 3400;
            player =  playerService.getPlayerById(id);
            if (player.isPresent()) {
                System.out.println("Player found: " + player.get().getFirstName() + " " + player.get().getLastName());
            } else {
                System.out.println("No player found for ID 1.");
            }

            System.out.println("Getting Stats by Name Dak Prescott");
            String name = "dakprescott";
            player = playerService.getPlayerByName(name);
            if (player.isPresent()) {
                System.out.println("Player found: " + player.get().getFirstName() + " " + player.get().getLastName());
            } else {
                System.out.println("No player found for ID 1.");
            }

            System.out.println("Getting Stats by Name Dak Prescott");
            List<Object> stats = playerStatsService.getStatsByName(name);
            System.out.println(stats);

            System.out.println("Command Line Test Complete");

        };
        }
    }
