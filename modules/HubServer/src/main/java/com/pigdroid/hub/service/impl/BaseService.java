package com.pigdroid.hub.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pigdroid.hub.dao.CRUDInterface;
import com.pigdroid.hub.dao.impl.BaseDAO;
import com.pigdroid.hub.model.persistent.PersistentResourceInterface;

@Service
@Transactional
public class BaseService <T extends PersistentResourceInterface> implements CRUDInterface<T> {
	
	protected CRUDInterface<T> dao = null;
	
	public void setDao(final BaseDAO<T> in) {
		this.dao = in;
	}

	@Override
	public T save(final T resource) {
		return dao.save(resource);
	}

	@Override
	public T get(final String id) {
		return dao.get(id);
	}

	@Override
	public <K extends T> List<T> list(final K example) {
		return dao.list(example);
	}

	@Override
	public <K extends T> void delete(final K example) {
		dao.delete(example);
	}

	@Override
	public void delete(final String id) {
		dao.delete(id);
	}

	@Override
	public <K extends T> int count(K example) {
		return dao.count(example);
	}

	@Override
	public <K extends T> T get(K example) {
		return dao.get(example);
	}

}
