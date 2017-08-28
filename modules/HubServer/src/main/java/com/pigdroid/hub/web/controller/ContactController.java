package com.pigdroid.hub.web.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pigdroid.hub.model.persistent.User;

@RestController
public class ContactController {

	@RequestMapping(value = "/contact/{id}", method = RequestMethod.GET)
	public @ResponseBody User get(@PathVariable("id") Long id) {
		return new User();
	}	

	@RequestMapping(value = "/contacts", method = RequestMethod.GET)
	public @ResponseBody List<User> search(@RequestBody(required = false) User example) {
		return Collections.emptyList();
	}

	@RequestMapping(value = "/user/{userId}/contacts", method = RequestMethod.GET)
	public @ResponseBody List<User> listContactsOf(@PathVariable("userId") Long id) {
		return Collections.emptyList();
	}

}
