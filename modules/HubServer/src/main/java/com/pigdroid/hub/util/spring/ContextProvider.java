package com.pigdroid.hub.util.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

 
public class ContextProvider implements ApplicationContextAware {
	
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		ContextProvider.applicationContext = applicationContext;
	}
	
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

}
