package dev.marquardt.FantasyFootballAPI.database;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PassingStatsRepository extends JpaRepository<PassingStats, Integer> {
}
