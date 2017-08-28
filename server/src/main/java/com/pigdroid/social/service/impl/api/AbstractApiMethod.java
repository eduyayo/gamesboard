package com.pigdroid.social.service.impl.api;

public abstract class AbstractApiMethod<Api, Response, Criteria> {
	
	public abstract Response execute(Api api, Criteria criteria);

}
