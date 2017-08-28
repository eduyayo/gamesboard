package com.pigdroid.hub.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;

import com.pigdroid.hub.dao.UserDAOInterface;
import com.pigdroid.hub.model.persistent.User;

@Repository
@org.springframework.transaction.annotation.Transactional(readOnly = false, propagation = Propagation.REQUIRED)
public class UserDAO extends BaseDAO<User> implements UserDAOInterface {

	public List<User> getWhoAdded(User user) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class);
		criteria.createCriteria("contacts").add(Restrictions.eq("id", user.getId()));
		return criteria.list();
	}

}
