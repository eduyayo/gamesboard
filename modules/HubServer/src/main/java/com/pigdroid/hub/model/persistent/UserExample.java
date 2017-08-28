package com.pigdroid.hub.model.persistent;

import java.util.List;

import com.pigdroid.hub.dao.impl.BaseDAO.ExampleLimit;

public class UserExample extends User implements ExampleLimit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8303082049287980172L;

	private List<String> emailIn;

	private List<String> emailNotIn;

	private Object emailLike;

	private String nickNameLike;

	public List<String> getEmailIn() {
		return emailIn;
	}

	public void setEmailIn(List<String> emailIn) {
		this.emailIn = emailIn;
	}

	public void setEmailLike(String searchTerm) {
		this.emailLike = searchTerm;
	}
	
	public Object getEmailLike() {
		return emailLike;
	}

	public void setNickNameLike(String searchTerm) {
		this.nickNameLike = searchTerm;
	}
	
	public String getNickNameLike() {
		return nickNameLike;
	}

	@Override
	public Integer getFirstResult() {
		return 0;
	}

	@Override
	public Integer getMaxResults() {
		return 5;
	}

	public List<String> getEmailNotIn() {
		return emailNotIn;
	}

	public void setEmailNotIn(List<String> emailNotIn) {
		this.emailNotIn = emailNotIn;
	}

}
