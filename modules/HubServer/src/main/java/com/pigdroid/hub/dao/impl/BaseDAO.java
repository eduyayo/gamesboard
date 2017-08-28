package com.pigdroid.hub.dao.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pigdroid.hub.dao.CRUDInterface;
import com.pigdroid.hub.model.persistent.PersistentResourceInterface;

@Repository
@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
public class BaseDAO<T extends PersistentResourceInterface> implements CRUDInterface<T> {
	
	public static interface ExampleLimit {
		Integer getFirstResult();
		Integer getMaxResults();
	}
	
	private Class<?> getExampleClass(Object example) {
		Class<?> clazz = example.getClass();
		Class<?> parent = clazz.getSuperclass();
		while (Object.class != parent) {
			clazz = parent;
			parent = parent.getSuperclass();
		}
		return clazz;
	}
	
	@Autowired
	protected SessionFactory sessionFactory;
	
	@Override
	public T save(final T resource) {
		if (resource.getTimestamp() == null) {
			resource.setTimestamp(new Timestamp(System.currentTimeMillis()));
		}
		sessionFactory.getCurrentSession().persist(resource);
		return resource;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(final String id) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria((Class)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
		criteria.add(Restrictions.eq("id", id));
		return (T) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K extends T> List<T> list(K example) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria((Class)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
		populateCriteriaForExample(criteria, example);
		return criteria.list();
	}
	
	private void populateCriteriaForExample(Criteria criteria, Object object) {
		Class<?> clazz = object.getClass();
		if (ExampleLimit.class.isAssignableFrom(clazz)) {
			ExampleLimit limit = (ExampleLimit) object;
			if (limit.getFirstResult() != null) {
				criteria.setFirstResult(limit.getFirstResult());
			}
			if (limit.getMaxResults() != null) {
				criteria.setMaxResults(limit.getMaxResults());
			}
		}
		List <Criterion> ors = new LinkedList<Criterion>();
		while (!Object.class.equals(clazz)) {
			Field[] fields = clazz.getDeclaredFields();
			int max = fields.length;
			for (int i = 0; i < max; i++) {
				Field field = fields[i];
				if ((field.getModifiers() & (Modifier.STATIC | Modifier.TRANSIENT)) == 0
						/* && (field.getType().isPrimitive()
								||field.getType().getName().startsWith("java.lang")) */) {
					field.setAccessible(true);
					String name = field.getName();
					Object value = null;
					try {
						value = field.get(object);
					} catch (IllegalArgumentException | IllegalAccessException e) {
					}
					if (value != null) {
						if (name.endsWith("From")) {

						} else if (name.endsWith("To")) {

						} else if (name.endsWith("NotIn")) {
							criteria.add(Restrictions.not(Restrictions.in(name.substring(0, name.length() - 5), (Collection<?>) value)));
						} else if (name.endsWith("In") && value instanceof Collection<?>) {
							criteria.add(Restrictions.in(name.substring(0, name.length() - 2), (Collection<?>) value));
						} else if (name.endsWith("Like")) {
							ors.add(Restrictions.ilike(name.substring(0, name.length() - 4), value.toString().toLowerCase() + "%"));
 						} else if (value.getClass().getName().startsWith("java.lang")){
							criteria.add(Restrictions.eq(name, value));
						}
					}
				}
			}
			clazz = clazz.getSuperclass();
		}
		switch (ors.size()) {
		case 0:
			break;
		case 1:
			criteria.add(ors.get(0));
			break;
		default:
			criteria.add(Restrictions.or((Criterion[]) ors.toArray(new Criterion[ors.size()])));
			break;
		}
	}

	@Override
	public <K extends T> void delete(K example) {
		for (T t : list(example)) {
			sessionFactory.getCurrentSession().delete(t);
		}
	}

	@Override
	public void delete(String id) {
		T t = get(id);
		if (t != null) {
			sessionFactory.getCurrentSession().delete(t);
		}
	}

	@Override
	public <K extends T> int count(K example) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(getExampleClass(example));
		populateCriteriaForExample(criteria, example);
		return countCriteria(criteria);
	}

	private int countCriteria(Criteria criteria) {
		criteria.setProjection(Projections.count("id"));
		int ret = ((Number) criteria.uniqueResult()).intValue();
		return ret;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K extends T> T get(K example) {
		T ret = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(getExampleClass(example));
		populateCriteriaForExample(criteria, example);
		if (countCriteria(criteria) == 1) {
			criteria.setProjection(null);
			ret = (T) criteria.uniqueResult();
		}
		return ret;
	}

}
