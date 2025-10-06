package dev.marquardt.FantasyFootballAPI.database;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrimmageStatsRepository extends JpaRepository<ScrimmageStats, Integer> {
}
