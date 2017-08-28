package com.pigdroid.hub.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pigdroid.hub.web.rest.dto.UserClientProfile;
import com.pigdroid.hub.web.rest.dto.UserForm;
import com.pigdroid.social.dto.RegistrationForm;
import com.pigdroid.social.model.user.User;
import com.pigdroid.social.service.UserService;
import com.pigdroid.social.service.exception.DuplicateEmailException;

@RestController
public class UserController {
	
	@Autowired
	UserService userService;

	@RequestMapping(value = "/register/user", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<UserClientProfile> registerUser(RegistrationForm userAccountData) {
		User user = null;
		ResponseEntity<UserClientProfile> ret = null;
		try {
			user = userService.registerNewUserAccount(userAccountData);
			ret = new ResponseEntity<UserClientProfile>(UserClientProfile.builder().from(user).build(), HttpStatus.CREATED);
		} catch (DuplicateEmailException e) {
			ret = new ResponseEntity<UserClientProfile>(HttpStatus.IM_USED);
		}
		
		return ret;
	}

	@RequestMapping(value = "/login/user", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<UserClientProfile> login(UserForm userForm) {
		userForm.setLogin(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
		User user = userService.login(userForm);
		ResponseEntity<UserClientProfile> ret = null;
		if (user != null) {
			ret = new ResponseEntity<UserClientProfile>(UserClientProfile.builder().from(user).build(), HttpStatus.ACCEPTED);
		} else {
			ret = new ResponseEntity<UserClientProfile>(HttpStatus.FORBIDDEN); //Should log in again
		}
		return ret;
	}
	
}
