package com.pigdroid.hub.service.impl;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pigdroid.hub.dao.CRUDInterface;
import com.pigdroid.hub.model.message.HubMessage;
import com.pigdroid.hub.model.message.MessageContext;
import com.pigdroid.hub.model.persistent.Message;
import com.pigdroid.hub.model.persistent.User;
import com.pigdroid.hub.model.persistent.UserExample;
import com.pigdroid.hub.service.MessageServiceInterface;
import com.pigdroid.hub.service.UserServiceInterface;

import flexjson.JSONSerializer;

@Service
@Transactional
public class MessageService extends BaseService<Message> implements MessageServiceInterface {

	private UserServiceInterface userService;
	
	public void setUserService(UserServiceInterface userService) {
		this.userService = userService;
	}
	
	public void setMessageDAO(CRUDInterface<Message> in) {
		this.dao = in;
	}

	@Override
	public void sendMessage(MessageContext context, HubMessage msg) {
		sendMessage(context, msg, false);
	}

	@Override
	public void sendMessage(MessageContext context, HubMessage msg, boolean persistOnFail) {
		List <String> failedRecipients = Collections.emptyList();
		if (persistOnFail) {
			failedRecipients = new LinkedList<String>();
		}

		JSONSerializer serializer = new JSONSerializer();
		for (String to : msg.getTo()) {
			PrintWriter writer = context.getWriter(to);
			if (writer != null) {
				try {
					writer.write(serializer.deepSerialize(msg));
					writer.flush();
				} catch (Exception e) {
					if (persistOnFail) {
						failedRecipients.add(to);
					}		
				}
			} else if (persistOnFail) {
				failedRecipients.add(to);
			}
		}
		if (persistOnFail && !failedRecipients.isEmpty()) {
			createMessage(msg, failedRecipients);
		}
	}

	private void createMessage(HubMessage msg, List<String> recipients) {
		Message message = new Message();
		message.setFrom(msg.getFrom());
		message.setPayload(msg.getPayload());
		message.setTimestamp(new Timestamp(System.currentTimeMillis()));
		UserExample example = new UserExample();
		example.setEmailIn(recipients);
		List<User> toList = userService.list(example);
		message.setUsers(new HashSet<User>(toList));
		message.setType(msg.getType());
		message.setWhat(msg.getWhat());
		save(message);
	}

	@Override
	public void sendErrorMessage(MessageContext context) {
		HubMessage msg = context.getMessage();
		msg.setWhat(msg.getType());
		msg.setType(HubMessage.TYPE_ERROR);
		JSONSerializer serializer = new JSONSerializer();
		PrintWriter writer = context.getWriter();
		writer.write(serializer.deepSerialize(msg));
		writer.flush();
	}

	@Override
	public void sendMessageToSession(MessageContext context, HubMessage message) {
		JSONSerializer serializer = new JSONSerializer();
		PrintWriter writer = context.getWriter();
		writer.write(serializer.deepSerialize(message));
		writer.flush();
	}
	
}
