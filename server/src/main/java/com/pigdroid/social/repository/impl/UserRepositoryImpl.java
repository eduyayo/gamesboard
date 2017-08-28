package com.pigdroid.social.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pigdroid.social.model.user.User;
import com.pigdroid.social.repository.UserRepository;
import com.pigdroid.social.repository.UserRepositoryCustom;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	@Transactional
	public User searchUserByEmailWithCollections(String email) {
		User user = userRepository.findByEmail(email);
		if (user != null) {
			user.getUsr().getContacts().size();
			user.getUsr().getGames().size();
			user.getUsr().getInverseContacts().size();
		}
		return user;
	}

}
