package com.pigdroid.hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pigdroid.hub.model.persistent.Usr;

/**
 * @author eduyayo@gmail.com
 */
public interface UsrRepository extends JpaRepository<Usr, Long> {

}
