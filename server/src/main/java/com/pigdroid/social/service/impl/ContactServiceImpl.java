package com.pigdroid.social.service.impl;

import com.pigdroid.social.service.ContactService;

public class ContactServiceImpl extends AbstractAPIService implements ContactService {

	public ContactServiceImpl() {
		super("Contact");
	}

	@Override
	public void list() {
		execute("List");
	}
	
}
