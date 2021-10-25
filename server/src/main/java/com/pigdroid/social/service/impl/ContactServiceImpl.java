package com.pigdroid.social.service.impl;

import org.springframework.stereotype.Service;

import com.pigdroid.social.service.ContactService;

@Service
public class ContactServiceImpl extends AbstractAPIService implements ContactService {

	public ContactServiceImpl() {
		super("Contact");
	}

	@Override
	public void list() {
		execute("List");
	}

}
