package com.pigdroid.social.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.google.api.Google;

import com.pigdroid.social.service.impl.api.AbstractApiMethod;

public class AbstractAPIService {

    @Autowired
    private ConnectionRepository connectionRepository;

    private String localResourceName;

    protected AbstractAPIService(String localResourceName) {
    	this.localResourceName = localResourceName;
    }

	protected <ReturnType> ReturnType execute(String methodName) {
		return execute(methodName, null);
	}

	@SuppressWarnings("unchecked")
	protected <ReturnType, Criteria> ReturnType execute(String methodName, Criteria criteria) {
		ReturnType ret = null;
		try {
			Connection<?> connection = connectionRepository.findPrimaryConnection(Google.class);
			if (connection != null) {
				AbstractApiMethod<Google, ReturnType, Criteria> method =
						(AbstractApiMethod<Google, ReturnType, Criteria>) Class.forName(ContactServiceImpl.class.getPackage().getName() + ".api.Google" + localResourceName + methodName).newInstance();
				ret = method.execute((Google) connection.getApi(), criteria);
			} else {
//				connection = connectionRepository.findPrimaryConnection(Facebook.class);
//				if (connection != null) {
//					AbstractApiMethod<Facebook, ReturnType, Criteria> method =
//							(AbstractApiMethod<Facebook, ReturnType, Criteria>) Class.forName(ContactServiceImpl.class.getPackage().getName() + ".api.Facebook" + localResourceName +  methodName).newInstance();
//					ret = method.execute((Facebook) connection.getApi(), criteria);
//				} else {
//					connection = connectionRepository.findPrimaryConnection(Twitter.class);
//					AbstractApiMethod<Twitter, ReturnType, Criteria> method =
//							(AbstractApiMethod<Twitter, ReturnType, Criteria>) Class.forName(ContactServiceImpl.class.getPackage().getName() + ".api.Twitter" + localResourceName +  methodName).newInstance();
//					ret = method.execute((Twitter) connection.getApi(), criteria);
//				}
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

}
