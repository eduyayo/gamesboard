package com.pigdroid.hub.dao;

import java.util.List;

import com.pigdroid.hub.model.persistent.User;

public interface UserDAOInterface extends CRUDInterface<User> {

	List<User> getWhoAdded(User user);

}
