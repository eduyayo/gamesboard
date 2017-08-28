package com.pigdroid.hub.service.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.sendgrid.SendGrid;
import com.pigdroid.hub.dao.CRUDInterface;
import com.pigdroid.hub.dao.UserDAOInterface;
import com.pigdroid.hub.model.message.HubMessage;
import com.pigdroid.hub.model.message.MessageContext;
import com.pigdroid.hub.model.persistent.User;
import com.pigdroid.hub.model.persistent.UserExample;
import com.pigdroid.hub.service.MessageServiceInterface;
import com.pigdroid.hub.service.UserServiceInterface;
import com.pigdroid.hub.service.exception.UserCredentialsException;

import flexjson.JSONSerializer;

@Service
@Transactional
public class UserService extends BaseService<User> implements UserServiceInterface {
	
	private MessageServiceInterface messageService;
	
	public void setMessageService(MessageServiceInterface messageService) {
		this.messageService = messageService;
	}

	public void setUserDAO(CRUDInterface<User> in) {
		this.dao = in;
	}
	
	private UserDAOInterface getUserDao() {
		return (UserDAOInterface) dao;
	}

	public User createUserIfNotExists(String email, String password, String alias) throws UserCredentialsException {
		User user = new User();
		user.setEmail(email);
		user.setNickName(alias);
		boolean exists = count(user) == 1;
		user.setPassword(password);
		boolean hasCredentials = count(user) == 1;
		if (!exists) {
			save(user);
		} else if (!hasCredentials) {
			throw new UserCredentialsException();
		}
		return user;
	}
	
	public boolean exists(String email) {
		User user = new User();
		user.setEmail(email);
		boolean exists = count(user) == 1;
		return exists;
	}
	
	public void doLogin(MessageContext context) {
		HubMessage msg = context.getMessage();
		User user = checkUser(msg);
		if (user != null) {
			context.setUserEmail(msg.getFrom());
			HubMessage newMsg = new HubMessage(HubMessage.TYPE_ACK,
					HubMessage.TYPE_LOGIN);
			newMsg.setPayload(createSerializer().deepSerialize(user));
			newMsg.addTo(msg.getFrom());
			messageService.sendMessage(context, newMsg);
		} else {
			// Login failed
			messageService.sendErrorMessage(context);
		}
	}

	private User checkUser(HubMessage msg) {
		String email = msg.getFrom();
		String password = msg.getWhat();
		User user = null;
		User example = new User();
		example.setPassword(password);
		example.setEmail(email);
		user = get(example);
		return user;	}

	public User checkUser(MessageContext context) {
		HubMessage msg = context.getMessage();
		return checkUser(msg);
	}

	public void doRegister(MessageContext context) {
		HubMessage msg = context.getMessage();
		String email = msg.getFrom();
		String password = msg.getWhat();
		String alias = msg.getPayload();
		HubMessage message = null;
		try {
			createUserIfNotExists(email, password, alias);
			message = new HubMessage(HubMessage.TYPE_ACK,
					HubMessage.TYPE_REGISTER);
		} catch (UserCredentialsException e) {
			message = new HubMessage(HubMessage.TYPE_ERROR,
					HubMessage.TYPE_REGISTER, "USER_EXISTS");
			messageService.sendMessage(context, message);
		}
		messageService.sendMessageToSession(context, message);
	}

	@Override
	public void doAddContact(MessageContext context) {
		HubMessage msg = context.getMessage();
		String email = msg.getTo().get(0);
		User addedUser = new User();
		addedUser.setEmail(email);
		addedUser = get(addedUser);
		if (addedUser != null) {
			JSONSerializer serializer = createSerializer();
			//Found the user, add him to the list of contacts for this session.
			HubMessage messageToUserInSession = new HubMessage(HubMessage.TYPE_ACK, HubMessage.TYPE_ADD_CONTACT);
			messageToUserInSession.addTo(context.getUserEmail());
			messageToUserInSession.setPayload(serializer.deepSerialize(addedUser));

			//Notify the destination of the contact about this event
			HubMessage messageToAddedUser = new HubMessage(HubMessage.TYPE_ADD_CONTACT);
			messageToAddedUser.addTo(addedUser.getEmail());
			User contextUser = getContextUser(context);
			contextUser.getContacts().add(addedUser);
			save(contextUser);

			//Avoid the serialization of private data.
			messageToAddedUser.setPayload(serializer.deepSerialize(contextUser));
			messageService.sendMessage(context, messageToAddedUser, true);
			messageService.sendMessage(context, messageToUserInSession);
		} else {
			HubMessage message = new HubMessage(HubMessage.TYPE_ERROR, HubMessage.TYPE_ADD_CONTACT);
			message.setTo(msg.getTo());
			message.setType("NOT_FOUND");
			messageService.sendMessage(context, message);
		}
	}

	private JSONSerializer createSerializer() {
		JSONSerializer serializer = new JSONSerializer().exclude("contacts", "password", "games", "messages");
		return serializer;
	}

	private User getContextUser(MessageContext context) {
		User userInSession = new User();
		userInSession.setEmail(context.getUserEmail());
		userInSession = get(userInSession);
		return userInSession;
	}

	@Override
	public void doRemoveContact(MessageContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doRetrieveRooster(MessageContext context) {
		User user = getContextUser(context);
		Set<User> contacts = new HashSet<User>(user.getContacts());
		Set<User> invited = new HashSet<User>(contacts);
		List<User> inverseRooster = getWhoAdded(user);

		invited.removeAll(inverseRooster);
		contacts.retainAll(inverseRooster);
		inverseRooster.removeAll(contacts);
		
		HubMessage msg = new HubMessage(HubMessage.TYPE_ACK, HubMessage.TYPE_REQUEST_ROOSTER);
		JSONSerializer serializer = createSerializer();
		StringBuilder builder = new StringBuilder();
		
		builder.append("[");
		builder.append(serializer.deepSerialize(invited)); // Users that I invited but have not added me.
		builder.append(",");
		builder.append(serializer.deepSerialize(contacts)); // Users present for game. Added and added back
		builder.append(",");
		builder.append(serializer.deepSerialize(inverseRooster)); // Users that have added me but I have not added back.
		builder.append("]");
		
		msg.setPayload(builder.toString());
		msg.addTo(user.getEmail());
		messageService.sendMessage(context, msg);
	}

	private List<User> getWhoAdded(User user) {
		UserDAOInterface dao = getUserDao();
		return dao.getWhoAdded(user);
	}

	@Override
	public void doListUsers(MessageContext context) {
		String searchTerm = context.getMessage().getPayload();
		UserExample userExample = new UserExample();
		userExample.setEmailLike(searchTerm);
		userExample.setNickNameLike(searchTerm);
		User user = getContextUser(context);
		List<String> emailNotIn = new LinkedList<String>();
		for (User contact : user.getContacts()) {
			emailNotIn.add(contact.getEmail());
		}
		emailNotIn.add(context.getUserEmail());
		userExample.setEmailNotIn(emailNotIn);
		List<User> list = list(userExample);

		HubMessage msg = new HubMessage(HubMessage.TYPE_ACK, HubMessage.TYPE_USER_LIST);
		msg.addTo(context.getUserEmail());
		JSONSerializer serializer = createSerializer();
		msg.setPayload(serializer.deepSerialize(list));
		messageService.sendMessage(context, msg);
	}

	@Override
	public void doSendPassword(MessageContext context) {
		HubMessage msg = context.getMessage();
		User user = getContextUser(context);
		if (user != null) {
			SendGrid sendgrid = new SendGrid("eduyayo", "mirapuru1");
			sendgrid.addTo(user.getEmail());
			sendgrid.setFrom("gameboard+pigdroid@gmail.com");
			sendgrid.setSubject("Game Board Password reminder!");
			sendgrid.setText("Your current password is: " + user.getPassword()
					+ ".");
			sendgrid.send();

			HubMessage newmsg = new HubMessage(HubMessage.TYPE_ACK, HubMessage.TYPE_SEND_PASS);
			newmsg.addTo(user.getEmail());
			messageService.sendMessage(context, newmsg);
		}
	}

	@Override
	public User getByEmail(String email) {
		UserExample example = new UserExample();
		example.setEmail(email);
		return get(example);
	}
	
}
