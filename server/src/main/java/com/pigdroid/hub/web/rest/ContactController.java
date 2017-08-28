package com.pigdroid.hub.web.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pigdroid.hub.web.rest.dto.UserClientProfile;
import com.pigdroid.social.model.user.User;
import com.pigdroid.social.service.UserService;

@RestController
public class ContactController {

	@Autowired
	UserService userService;

	@RequestMapping(value = "/user/contacts", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<UserClientProfile>> list() {
		String sessionLogin = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<UserClientProfile>  ret = userService.listContacts(sessionLogin);
		return new ResponseEntity<List<UserClientProfile>>(ret, HttpStatus.OK);
	}

	@RequestMapping(value = "/contacts", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<UserClientProfile>> listContacts(@RequestParam(required = true, value = "email") String email) {
		List<UserClientProfile>  ret = Collections.emptyList();
		List<User> list = userService.search(email, (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (!list.isEmpty()) {
			ret = new ArrayList<UserClientProfile>();
			for (User user : list) {
				ret.add(UserClientProfile.builder().from(user).accepted(true).build());
			}
		}
		return new ResponseEntity<List<UserClientProfile>>(ret, HttpStatus.OK);
	}

	/**
	 *	goes like /users?email=eduyayo@gmail.com&name=edu
	 * 
	 * @param name
	 * @param email
	 * @return
	 */
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<UserClientProfile>> search(
			@RequestParam(value = "name", required = false) final String name,
			@RequestParam(value = "email", required = false) final String email) {
		List<UserClientProfile> list = Collections.emptyList();
		ResponseEntity<List<UserClientProfile>> ret = new ResponseEntity<List<UserClientProfile>>(list, HttpStatus.OK);
		return ret;
	}

	@RequestMapping(value = "/contact", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<UserClientProfile> add(@RequestParam(required = true, value = "email") String email) {
		String login = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = userService.addContact(email, login);
		return new ResponseEntity<UserClientProfile>(UserClientProfile.builder().from(user).accepted(true).build(), HttpStatus.OK);
	}

}
