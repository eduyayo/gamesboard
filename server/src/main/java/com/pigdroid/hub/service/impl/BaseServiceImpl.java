package com.pigdroid.hub.service.impl;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pigdroid.social.model.BaseEntity;

@Service
@Transactional
public class BaseServiceImpl <T extends BaseEntity<Long>> {

	protected PagingAndSortingRepository<T, String> repository = null;

	public void setDao(final JpaRepository<T, String> repository) {
		this.repository = repository;
	}

	public T get(String id) {
		Optional<T> ret = repository.findById(id);
		return ret.isPresent() ? ret.get() : null;
	}

	public T save(T t) {
		return repository.save(t);
	}

}
