package dev.marquardt.FantasyFootballAPI.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Integer> {
    List<Player> findByPositionIgnoreCase(String position);
}
