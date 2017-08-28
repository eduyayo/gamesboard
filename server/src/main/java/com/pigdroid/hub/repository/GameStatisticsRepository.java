package com.pigdroid.hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pigdroid.hub.model.persistent.GameStatistics;

/**
 * @author eduyayo@gmail.com
 */
public interface GameStatisticsRepository extends JpaRepository<GameStatistics, Long> {

}
