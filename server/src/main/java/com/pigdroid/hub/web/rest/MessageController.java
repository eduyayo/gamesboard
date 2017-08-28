package com.pigdroid.hub.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pigdroid.hub.model.message.HubMessage;
import com.pigdroid.hub.service.MessageService;

@RestController
public class MessageController {
	
	@Autowired
	MessageService messageService;
	
	@RequestMapping(value = "/message", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> sendMessage(@RequestBody HubMessage message) {
//		String sessionLogin = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return new ResponseEntity<String>(messageService.handleMessage(message), HttpStatus.OK);
	}

}
