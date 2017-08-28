package com.pigdroid.hub.dao;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.pigdroid.hub.model.persistent.PersistentResourceInterface;

@Transactional 
public interface CRUDInterface<T extends PersistentResourceInterface> {
	
	 T save(final T resource);
	 T get(final String id);
	 <K extends T> T get(final K example);
	 <K extends T> List<T> list(final K example);
	 <K extends T> int count(final K example);
	 <K extends T> void delete(final K example);
	 void delete(final String id);

}
