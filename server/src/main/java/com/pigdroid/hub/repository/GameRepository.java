package com.pigdroid.hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pigdroid.hub.model.persistent.Game;

/**
 * @author eduyayo@gmail.com
 */
public interface GameRepository extends JpaRepository<Game, Long> {

	@Override
	public boolean existsById(Long id);

}
