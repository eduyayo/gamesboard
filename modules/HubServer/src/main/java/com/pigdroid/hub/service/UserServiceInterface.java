package com.pigdroid.hub.service;

import com.pigdroid.hub.dao.CRUDInterface;
import com.pigdroid.hub.model.message.MessageContext;
import com.pigdroid.hub.model.persistent.User;
import com.pigdroid.hub.service.exception.UserCredentialsException;

public interface UserServiceInterface extends CRUDInterface<User> {

	User createUserIfNotExists(String email, String password, String alias)
			throws UserCredentialsException;

	void doRegister(MessageContext context);

	User checkUser(MessageContext context);

	void doLogin(MessageContext context);

	void doAddContact(MessageContext context);

	void doRemoveContact(MessageContext context);

	void doRetrieveRooster(MessageContext context);

	void doListUsers(MessageContext context);

	void doSendPassword(MessageContext context);
	
	User getByEmail(String email);

}
